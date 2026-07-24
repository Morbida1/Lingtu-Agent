package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.SpotMapper;
import com.morbid.lingtuagent.model.dto.SpotDTO;
import com.morbid.lingtuagent.model.entity.Spot;
import com.morbid.lingtuagent.model.vo.SpotVO;
import com.morbid.lingtuagent.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl extends ServiceImpl<SpotMapper, Spot> implements SpotService {
    @Override
    public SpotVO createSpot(SpotDTO spotDTO) {
        Spot spot = new Spot();
        BeanUtils.copyProperties(spotDTO, spot);
        this.save(spot);
        return convertToVO(spot);
    }

    @Override
    public SpotVO updateSpot(Long id, SpotDTO spotDTO) {
        Spot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }
        BeanUtils.copyProperties(spotDTO, spot);
        this.updateById(spot);
        return convertToVO(spot);
    }

    @Override
    public void deleteSpot(Long id) {
        Spot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }
        LambdaUpdateWrapper<Spot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Spot::getId, id)
               .set(Spot::getDeleted, 1)
               .set(Spot::getDeleteTime, LocalDateTime.now());
        this.update(wrapper);
    }

    @Override
    public SpotVO getSpotVOById(Long id) {
        Spot spot = this.getById(id);
        if (spot == null) {
            throw new BusinessException("景点不存在");
        }
        return convertToVO(spot);
    }
    @Override
    public List<SpotVO> listAllSpot() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    @Override
    public IPage<SpotVO> page(int pageNum, int pageSize, String keyword) {
        Page<Spot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Spot> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Spot::getName, keyword);
        }
        IPage<Spot> spotPage = this.page(page, wrapper);
        return spotPage.convert(this::convertToVO);
    }
    @Override
    public List<SpotVO> listByCityId(Long cityId) {
        LambdaQueryWrapper<Spot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Spot::getCityId, cityId);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    private SpotVO convertToVO(Spot spot) {
        SpotVO vo = new SpotVO();
        BeanUtils.copyProperties(spot, vo);
        return vo;
    }

    @Override
    public List<SpotVO> listDeleted() {
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