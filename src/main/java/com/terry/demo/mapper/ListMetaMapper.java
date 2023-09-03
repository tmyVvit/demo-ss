package com.terry.demo.mapper;

import com.terry.demo.model.ListMetaModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ListMetaMapper {

    ListMetaModel selectById(Long id);
}
