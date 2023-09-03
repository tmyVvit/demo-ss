package com.terry.demo.mapper;

import com.terry.demo.model.ListInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ListInfoMapper {

    ListInfoModel selectByIdAndCreateTime(Long id, LocalDateTime createTime);

    int insertListInfo(@Param("m") ListInfoModel listInfoModel);
}
