package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.HotelMapper;
import com.morbid.lingtuagent.model.dto.HotelDTO;
import com.morbid.lingtuagent.model.entity.Hotel;
import com.morbid.lingtuagent.model.vo.HotelVO;
import com.morbid.lingtuagent.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

    @Override
    public HotelVO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = new Hotel();
        BeanUtils.copyProperties(hotelDTO, hotel);
        this.save(hotel);
        return convertToVO(hotel);
    }
    @Override
    public HotelVO updateHotel(Long id, HotelDTO hotelDTO) {
        Hotel hotel = this.getById(id);
        if (hotel == null) {
            throw new BusinessException("酒店不存在");
        }
        BeanUtils.copyProperties(hotelDTO, hotel);
        this.updateById(hotel);
        return convertToVO(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException("酒店不存在");
        }

    }
    @Override
    public HotelVO getHotelVOById(Long id) {
        Hotel hotel = this.getById(id);
        if (hotel == null) {
            throw new BusinessException("酒店不存在");
        }
        return convertToVO(hotel);
    }
    @Override
    public List<HotelVO> listAllHotel() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    @Override
    public IPage<HotelVO> page(int pageNum, int pageSize, String keyword) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Hotel::getName, keyword);
        }
        IPage<Hotel> hotelIPage = this.page(page, wrapper);
        return hotelIPage.convert(this::convertToVO);
    }
    @Override
    public List<HotelVO> listByCityId(Long cityId) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getCityId, cityId);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    private HotelVO convertToVO(Hotel hotel) {
        HotelVO vo = new HotelVO();
        BeanUtils.copyProperties(hotel, vo);
        return vo;
    }
}