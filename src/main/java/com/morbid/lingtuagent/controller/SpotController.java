package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.dto.SpotDTO;
import com.morbid.lingtuagent.model.vo.SpotVO;
import com.morbid.lingtuagent.service.SpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spot")
@RequiredArgsConstructor
public class SpotController {
    private final SpotService spotService;
    @PostMapping
    public Result<SpotVO> create(@Valid @RequestBody SpotDTO dto) {
        return Result.success(spotService.createSpot(dto));
    }
    @PutMapping("/{id}")
    public Result<SpotVO> update(@PathVariable Long id, @Valid @RequestBody SpotDTO dto) {
        return Result.success(spotService.updateSpot(id, dto));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        spotService.deleteSpot(id);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result<SpotVO> get(@PathVariable Long id) {
        return Result.success(spotService.getSpotVOById(id));
    }
    @GetMapping("/list")
    public Result<List<SpotVO>> list() {
        return Result.success(spotService.listAllSpot());
    }
    @GetMapping("/page")
    public Result<IPage<SpotVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(spotService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/list/city/{cityId}")
    public Result<List<SpotVO>> listByCity(@PathVariable Long cityId) {
        return Result.success(spotService.listByCityId(cityId));
    }

}