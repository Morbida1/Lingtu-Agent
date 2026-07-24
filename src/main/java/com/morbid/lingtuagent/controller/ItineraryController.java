package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.dto.ItineraryDTO;
import com.morbid.lingtuagent.model.vo.ItineraryVO;
import com.morbid.lingtuagent.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@RequiredArgsConstructor
public class ItineraryController {
    private final ItineraryService itineraryService;
    @PostMapping
    public Result<ItineraryVO> create(@Valid @RequestBody ItineraryDTO dto) {
        return Result.success(itineraryService.createItinerary(dto));
    }
    @PutMapping("/{id}")
    public Result<ItineraryVO> update(@PathVariable Long id, @Valid @RequestBody ItineraryDTO dto) {
        return Result.success(itineraryService.updateItinerary(id, dto));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        itineraryService.deleteItinerary(id);
        return Result.success();
    }

    @GetMapping("/deleted")
    public Result<List<ItineraryVO>> listDeleted() {
        return Result.success(itineraryService.listDeleted());
    }

    @PutMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        itineraryService.restore(id);
        return Result.success();
    }

    @DeleteMapping("/{id}/physical")
    public Result<Void> physicalDelete(@PathVariable Long id) {
        itineraryService.physicalDelete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ItineraryVO> get(@PathVariable Long id) {
        return Result.success(itineraryService.getItineraryById(id));
    }
    @GetMapping("/list")
    public Result<List<ItineraryVO>> list() {
        return Result.success(itineraryService.listAllItinerary());
    }
    @GetMapping("/page")
    public Result<IPage<ItineraryVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(itineraryService.pageItinerary(pageNum, pageSize, keyword));
    }
    @GetMapping("/list/user/{userId}")
    public Result<List<ItineraryVO>> listByUserId(@PathVariable Long userId) {
        return Result.success(itineraryService.listByUserId(userId));
    }
    @GetMapping("/list/city/{cityId}")
    public Result<List<ItineraryVO>> listByCityId(@PathVariable Long cityId) {
        return Result.success(itineraryService.listByCityId(cityId));
    }
}