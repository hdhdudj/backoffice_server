package io.spring.model.ship.idclass;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class LsshpdId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;
    private String shipId;
    private String shipSeq;
}
