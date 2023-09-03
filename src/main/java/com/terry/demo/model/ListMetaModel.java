package com.terry.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListMetaModel extends BaseModel implements Serializable {

    private Long id;

    private String listName;

    private String listDescription;

    private String encryptType;

}
