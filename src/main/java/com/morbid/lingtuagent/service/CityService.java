package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.CityDTO;
import com.morbid.lingtuagent.model.entity.City;
import com.morbid.lingtuagent.model.vo.CityVO;

import java.util.List;

public interface CityService extends IService<City> {
    CityVO createCity(CityDTO cityDto);
    CityVO updateCity(Long id, CityDTO cityDto);
    void deleteCity(Long id);
    CityVO getCityVOById(Long id);
    List<CityVO> listAllCity();
    IPage<CityVO> page(int pageNum, int pageSize, String keyword);

    List<CityVO> listDeleted();
    void restore(Long id);
    void physicalDelete(Long id);
}