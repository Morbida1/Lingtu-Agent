package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.dto.HotelDTO;
import com.morbid.lingtuagent.model.vo.HotelVO;
import com.morbid.lingtuagent.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;
    @PostMapping
    public Result<HotelVO> create(@Valid @RequestBody HotelDTO dto) {
        return Result.success(hotelService.createHotel(dto));
    }
    @PutMapping("/{id}")
    public Result<HotelVO> update(@PathVariable Long id, @Valid @RequestBody HotelDTO dto) {
        return Result.success(hotelService.updateHotel(id, dto));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result<HotelVO> get(@PathVariable Long id) {
        return Result.success(hotelService.getHotelVOById(id));
    }
    @GetMapping("/list")
    public Result<List<HotelVO>> list() {
        return Result.success(hotelService.listAllHotel());
    }
    @GetMapping("/page")
    public Result<IPage<HotelVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(hotelService.page(pageNum, pageSize, keyword));
    }
    @GetMapping("/list/city/{cityId}")
    public Result<List<HotelVO>> listByCity(@PathVariable Long cityId) {
        return Result.success(hotelService.listByCityId(cityId));
    }
}
