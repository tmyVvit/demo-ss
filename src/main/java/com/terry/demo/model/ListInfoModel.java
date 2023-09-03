package com.terry.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListInfoModel extends BaseModel implements Serializable {

    private Long id;

    private Long listId;

    private String name;
}
