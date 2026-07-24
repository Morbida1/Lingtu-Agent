package com.morbid.lingtuagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morbid.lingtuagent.model.entity.Hotel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {

    @Delete("DELETE FROM hotel WHERE id = #{id}")
    int physicalDeleteById(@Param("id") Long id);

    @Select("SELECT * FROM hotel WHERE deleted = 1")
    List<Hotel> selectDeleted();

    @Update("UPDATE hotel SET deleted = 0, update_time = NOW() WHERE id = #{id}")
    int restoreById(@Param("id") Long id);
}