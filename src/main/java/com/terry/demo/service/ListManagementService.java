package com.terry.demo.service;

import com.terry.demo.model.ListInfoModel;
import com.terry.demo.model.ListMetaModel;

import java.time.LocalDateTime;

public interface ListManagementService {

    ListInfoModel selectListInfo(Long id, LocalDateTime createTime);

    ListMetaModel selectListMeta(Long id);

    int insertListInfo(ListInfoModel listInfoModel);
}
