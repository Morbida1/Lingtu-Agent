package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.CityMapper;
import com.morbid.lingtuagent.model.dto.CityDTO;
import com.morbid.lingtuagent.model.entity.City;
import com.morbid.lingtuagent.model.vo.CityVO;
import com.morbid.lingtuagent.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Override
    public CityVO createCity(CityDTO dto) {
        City city = new City();
        BeanUtils.copyProperties(dto, city);
        this.save(city);
        return convertToVO(city);
    }

    @Override
    public CityVO updateCity(Long id, CityDTO dto) {
        City city = this.getById(id);
        if (city == null) {
            throw new BusinessException("城市不存在");
        }
        BeanUtils.copyProperties(dto, city);
        this.updateById(city);
        return convertToVO(city);
    }

    @Override
    public void deleteCity(Long id) {
        City city = this.getById(id);
        if (city == null) {
            throw new BusinessException("城市不存在");
        }
        LambdaUpdateWrapper<City> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(City::getId, id)
               .set(City::getDeleted, 1)
               .set(City::getDeleteTime, LocalDateTime.now());
        this.update(wrapper);
    }

    @Override
    public CityVO getCityVOById(Long id) {
        City city = this.getById(id);
        if (city == null) {
            throw new BusinessException("城市不存在");
        }
        return convertToVO(city);
    }

    @Override
    public List<CityVO> listAllCity() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<CityVO> page(int pageNum, int pageSize, String keyword) {
        Page<City> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(City::getName, keyword);
        }
        IPage<City> cityPage = this.page(page, wrapper);
        return cityPage.convert(this::convertToVO);
    }

    private CityVO convertToVO(City city) {
        CityVO vo = new CityVO();
        BeanUtils.copyProperties(city, vo);
        return vo;
    }

    @Override
    public List<CityVO> listDeleted() {
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