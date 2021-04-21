package io.spring.model.goods;

import lombok.Data;

import java.io.Serializable;

@Data
public class ItvariId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    private long assortId;
    private int seq;
}

