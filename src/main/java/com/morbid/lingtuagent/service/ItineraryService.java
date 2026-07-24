package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.ItineraryDTO;
import com.morbid.lingtuagent.model.entity.Itinerary;
import com.morbid.lingtuagent.model.vo.ItineraryVO;

import java.util.List;

public interface ItineraryService extends IService<Itinerary> {
    ItineraryVO createItinerary(ItineraryDTO itineraryDTO);
    ItineraryVO updateItinerary(Long id, ItineraryDTO itineraryDTO);
    void deleteItinerary(Long id);
    ItineraryVO getItineraryById(Long id);
    List<ItineraryVO> listAllItinerary();
    IPage<ItineraryVO> pageItinerary(int pageNum, int pageSize, String keyword);
    List<ItineraryVO> listByUserId(Long userId);  // 按用户查行程
    List<ItineraryVO> listByCityId(Long cityId);

    List<ItineraryVO> listDeleted();
    void restore(Long id);
    void physicalDelete(Long id);
}