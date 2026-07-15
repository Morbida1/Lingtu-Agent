package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.dto.CityDTO;
import com.morbid.lingtuagent.model.vo.CityVO;
import com.morbid.lingtuagent.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;
    @PostMapping
    public Result<CityVO> create(@Valid @RequestBody  CityDTO dto) {
        return Result.success(cityService.createCity(dto));
    }
    @PutMapping("/{id}")
    public Result<CityVO> update(@PathVariable Long id, @Valid @RequestBody CityDTO dto) {
        return Result.success(cityService.updateCity(id, dto));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cityService.deleteCity(id);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result<CityVO> getById(@PathVariable Long id) {
        return Result.success(cityService.getCityVOById(id));
    }
    @GetMapping("/list")
    public Result<List<CityVO>> list() {
        return Result.success(cityService.listAllCity());
    }
    @GetMapping("/page")
    public Result<IPage<CityVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam (required = false)String keyword) {
        return Result.success(cityService.page(pageNum,pageSize,keyword));
    }
}
