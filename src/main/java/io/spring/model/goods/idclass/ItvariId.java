package io.spring.model.goods.idclass;

import lombok.Data;

import java.io.Serializable;

@Data
public class ItvariId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    private String assortId;
    private String seq;
}

