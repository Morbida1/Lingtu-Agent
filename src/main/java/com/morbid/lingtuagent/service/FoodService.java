package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.FoodDTO;
import com.morbid.lingtuagent.model.entity.Food;
import com.morbid.lingtuagent.model.vo.FoodVO;

import java.util.List;

public interface FoodService extends IService<Food> {
    FoodVO createFood(FoodDTO foodDTO);
    FoodVO updateFood(Long id, FoodDTO foodDTO);
    void deleteFood(Long id);
    FoodVO getFoodVOById(Long id);
    List<FoodVO> listAllFood();
    IPage<FoodVO> page(int pageNum, int pageSize, String keyword);
    List<FoodVO> listByCityId(Long cityId);
    List<FoodVO> listByCategory(String category);

    List<FoodVO> listDeleted();
    void restore(Long id);
    void physicalDelete(Long id);
}