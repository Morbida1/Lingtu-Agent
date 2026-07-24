package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.ItineraryDayMapper;
import com.morbid.lingtuagent.mapper.ItineraryItemMapper;
import com.morbid.lingtuagent.mapper.ItineraryMapper;
import com.morbid.lingtuagent.model.dto.ItineraryDTO;
import com.morbid.lingtuagent.model.dto.ItineraryDayDTO;
import com.morbid.lingtuagent.model.dto.ItineraryItemDTO;
import com.morbid.lingtuagent.model.entity.Itinerary;
import com.morbid.lingtuagent.model.entity.ItineraryDay;
import com.morbid.lingtuagent.model.entity.ItineraryItem;
import com.morbid.lingtuagent.model.vo.ItineraryDayVO;
import com.morbid.lingtuagent.model.vo.ItineraryItemVO;
import com.morbid.lingtuagent.model.vo.ItineraryVO;
import com.morbid.lingtuagent.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl extends ServiceImpl<ItineraryMapper, Itinerary> implements ItineraryService {

    private final ItineraryDayMapper dayMapper;
    private final ItineraryItemMapper itemMapper;

    @Override
    @Transactional
    public ItineraryVO createItinerary(ItineraryDTO dto) {
        Itinerary itinerary = new Itinerary();
        BeanUtils.copyProperties(dto, itinerary);
        save(itinerary);
        saveDaysAndItems(itinerary.getId(), dto.getDayList());
        return getItineraryById(itinerary.getId());
    }

    @Override
    @Transactional
    public ItineraryVO updateItinerary(Long id, ItineraryDTO dto) {
        Itinerary itinerary = getById(id);
        if (itinerary == null) {
            throw new BusinessException( "行程不存在");
        }
        BeanUtils.copyProperties(dto, itinerary);
        updateById(itinerary);
        // 先删旧的日程和项目，再重新保存
        deleteDaysAndItems(id);
        saveDaysAndItems(id, dto.getDayList());
        return getItineraryById(id);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long id) {
        Itinerary itinerary = this.getById(id);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }
        LambdaUpdateWrapper<Itinerary> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Itinerary::getId, id)
               .set(Itinerary::getDeleted, 1)
               .set(Itinerary::getDeleteTime, LocalDateTime.now());
        this.update(wrapper);
        deleteDaysAndItems(id);
    }

    @Override
    public ItineraryVO getItineraryById(Long id) {
        Itinerary itinerary = getById(id);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }
        ItineraryVO vo = convertToVO(itinerary);
        vo.setDayList(getDayVOList(id));
        return vo;
    }

    @Override
    public List<ItineraryVO> listAllItinerary() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<ItineraryVO> pageItinerary(int pageNum, int pageSize, String keyword) {
        IPage<Itinerary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Itinerary::getTitle, keyword);
        }
        IPage<Itinerary> itineraryIPage = this.page(page, wrapper);
        return itineraryIPage.convert(this::convertToVO);
    }

    @Override
    public List<ItineraryVO> listByCityId(Long cityId) {
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getCityId, cityId);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItineraryVO> listByUserId(Long userId) {
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getUserId, userId);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    private void saveDaysAndItems(Long itineraryId, List<ItineraryDayDTO> dayList) {
        if (dayList == null || dayList.isEmpty()) return;
        for (ItineraryDayDTO dayDTO : dayList) {
            ItineraryDay day = new ItineraryDay();
            day.setItineraryId(itineraryId);
            day.setDayNumber(dayDTO.getDayNumber());
            day.setDescription(dayDTO.getDescription());
            dayMapper.insert(day);

            if (dayDTO.getItemList() != null) {
                for (ItineraryItemDTO itemDTO : dayDTO.getItemList()) {
                    ItineraryItem item = new ItineraryItem();
                    item.setItineraryDayId(day.getId());
                    item.setItemType(itemDTO.getItemType());
                    item.setItemId(itemDTO.getItemId());
                    item.setSortOrder(itemDTO.getSortOrder());
                    item.setNote(itemDTO.getNote());
                    itemMapper.insert(item);
                }
            }
        }
    }

    private void deleteDaysAndItems(Long itineraryId) {
        LambdaQueryWrapper<ItineraryDay> dayWrapper = new LambdaQueryWrapper<>();
        dayWrapper.eq(ItineraryDay::getItineraryId, itineraryId);
        List<ItineraryDay> days = dayMapper.selectList(dayWrapper);
        for (ItineraryDay day : days) {
            LambdaQueryWrapper<ItineraryItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(ItineraryItem::getItineraryDayId, day.getId());
            itemMapper.delete(itemWrapper);
        }
        dayMapper.delete(dayWrapper);
    }

    private List<ItineraryDayVO> getDayVOList(Long itineraryId) {
        LambdaQueryWrapper<ItineraryDay> dayWrapper = new LambdaQueryWrapper<>();
        dayWrapper.eq(ItineraryDay::getItineraryId, itineraryId)
                  .orderByAsc(ItineraryDay::getDayNumber);
        List<ItineraryDay> days = dayMapper.selectList(dayWrapper);
        return days.stream().map(day -> {
            ItineraryDayVO dayVO = new ItineraryDayVO();
            BeanUtils.copyProperties(day, dayVO);

            LambdaQueryWrapper<ItineraryItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(ItineraryItem::getItineraryDayId, day.getId())
                       .orderByAsc(ItineraryItem::getSortOrder);
            List<ItineraryItem> items = itemMapper.selectList(itemWrapper);
            dayVO.setItemList(items.stream().map(item -> {
                ItineraryItemVO itemVO = new ItineraryItemVO();
                BeanUtils.copyProperties(item, itemVO);
                return itemVO;
            }).collect(Collectors.toList()));

            return dayVO;
        }).collect(Collectors.toList());
    }

    private ItineraryVO convertToVO(Itinerary itinerary) {
        ItineraryVO vo = new ItineraryVO();
        BeanUtils.copyProperties(itinerary, vo);
        return vo;
    }

    @Override
    public List<ItineraryVO> listDeleted() {
        return baseMapper.selectDeleted().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void restore(Long id) {
        if (baseMapper.restoreById(id) == 0) {
            throw new BusinessException("恢复失败");
        }
    }

    @Override
    public void physicalDelete(Long id) {
        if (baseMapper.physicalDeleteById(id) == 0) {
            throw new BusinessException("物理删除失败，数据不存在");
        }
    }
}