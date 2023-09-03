package com.terry.demo.service;

import com.terry.demo.mapper.ListMetaMapper;
import com.terry.demo.model.ListInfoModel;
import com.terry.demo.model.ListMetaModel;
import com.terry.demo.mapper.ListInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ListManagementServiceImpl implements ListManagementService {

    @Autowired
    private ListInfoMapper listInfoMapper;


    @Autowired
    private ListMetaMapper listMetaMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public ListInfoModel selectListInfo(Long id, LocalDateTime createTime) {
        return listInfoMapper.selectByIdAndCreateTime(id, createTime);
    }

    @Override
    public ListMetaModel selectListMeta(Long id) {
        return listMetaMapper.selectById(id);
    }

    @Override
    public int insertListInfo(ListInfoModel listInfoModel) {
        return listInfoMapper.insertListInfo(listInfoModel);
    }
}
