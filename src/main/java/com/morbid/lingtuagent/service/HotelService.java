package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.HotelDTO;
import com.morbid.lingtuagent.model.entity.Hotel;
import com.morbid.lingtuagent.model.vo.HotelVO;

import java.util.List;

public interface HotelService extends IService<Hotel> {
    HotelVO createHotel(HotelDTO hotelDTO);
    HotelVO updateHotel(Long id, HotelDTO hotelDTO);
    void deleteHotel(Long id);
    HotelVO getHotelVOById(Long id);
    List<HotelVO> listAllHotel();
    IPage<HotelVO> page(int pageNum, int pageSize,String keyword);
    List<HotelVO> listByCityId(Long cityId);

    List<HotelVO> listDeleted();
    void restore(Long id);
    void physicalDelete(Long id);
}