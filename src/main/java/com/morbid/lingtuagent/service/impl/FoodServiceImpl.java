package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.FoodMapper;
import com.morbid.lingtuagent.model.dto.FoodDTO;
import com.morbid.lingtuagent.model.entity.Food;
import com.morbid.lingtuagent.model.vo.FoodVO;
import com.morbid.lingtuagent.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public FoodVO createFood(FoodDTO foodDTO) {
        Food food = new Food();
        BeanUtils.copyProperties(foodDTO, food);
        this.save(food);
        return convertToVO(food);
    }
    @Override
    public FoodVO updateFood(Long id, FoodDTO foodDTO) {
        Food food = this.getById(id);
        BeanUtils.copyProperties(foodDTO, food);
        this.updateById(food);
        return convertToVO(food);
    }
    @Override
    public void deleteFood(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException("美食不存在");
        }
    }
    @Override
    public FoodVO getFoodVOById(Long id) {
        Food food = this.getById(id);
        if (food == null) {
            throw new BusinessException("美食不存在");
        }
        return convertToVO(food);
    }
    @Override
    public List<FoodVO> listAllFood() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    @Override
    public IPage<FoodVO> page(int pageNum, int pageSize, String keyword) {
        Page<Food> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Food::getName, keyword);
        }
        IPage<Food> foodIPage = this.page(page, wrapper);
        return foodIPage.convert(this::convertToVO);
    }
    @Override
    public List<FoodVO> listByCityId(Long cityId) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getCityId, cityId);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    @Override
    public List<FoodVO> listByCategory(String category) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getCategory, category);
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    private FoodVO convertToVO(Food food) {
        FoodVO vo = new FoodVO();
        BeanUtils.copyProperties(food, vo);
        return vo;
    }
}