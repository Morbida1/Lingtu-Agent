package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.SpotDTO;
import com.morbid.lingtuagent.model.entity.Spot;
import com.morbid.lingtuagent.model.vo.SpotVO;

import java.util.List;

public interface SpotService extends IService<Spot> {
    SpotVO createSpot(SpotDTO spotDTO);
    SpotVO updateSpot(Long id, SpotDTO spotDTO);
    void deleteSpot(Long id);
    SpotVO getSpotVOById(Long id);
    List<SpotVO> listAllSpot();
    IPage<SpotVO> page(int pageNum, int pageSize, String keyword);
    List<SpotVO> listByCityId(Long cityId);

    List<SpotVO> listDeleted();
    void restore(Long id);
    void physicalDelete(Long id);
}