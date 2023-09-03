package com.terry.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseModel {

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    private String createUser;

    private String updateUser;
}
