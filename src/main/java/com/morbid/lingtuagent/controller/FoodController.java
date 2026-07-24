package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.dto.FoodDTO;
import com.morbid.lingtuagent.model.vo.FoodVO;
import com.morbid.lingtuagent.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;
    @PostMapping
    public Result<FoodVO> create(@Valid @RequestBody FoodDTO dto) {
        return Result.success(foodService.createFood(dto));
    }
    @PutMapping("/{id}")
    public Result<FoodVO> update(@PathVariable Long id, @Valid @RequestBody FoodDTO dto) {
        return Result.success(foodService.updateFood(id, dto));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        foodService.deleteFood(id);
        return Result.success();
    }

    @GetMapping("/deleted")
    public Result<List<FoodVO>> listDeleted() {
        return Result.success(foodService.listDeleted());
    }

    @PutMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        foodService.restore(id);
        return Result.success();
    }

    @DeleteMapping("/{id}/physical")
    public Result<Void> physicalDelete(@PathVariable Long id) {
        foodService.physicalDelete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FoodVO> get(@PathVariable Long id) {
        return Result.success(foodService.getFoodVOById(id));
    }
    @GetMapping("/list")
    public Result<List<FoodVO>> list() {
        return Result.success(foodService.listAllFood());
    }
    @GetMapping("/page")
    public Result<IPage<FoodVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(foodService.page(pageNum, pageSize, keyword));
    }
    @GetMapping("/list/city/{cityId}")
    public Result<List<FoodVO>> listByCity(@PathVariable Long cityId) {
        return Result.success(foodService.listByCityId(cityId));
    }
    @GetMapping("/list/category/{category}")
    public Result<List<FoodVO>> listByCategory(@PathVariable String category) {
        return Result.success(foodService.listByCategory(category));
    }
}