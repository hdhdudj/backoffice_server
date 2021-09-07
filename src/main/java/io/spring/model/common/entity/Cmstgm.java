package io.spring.model.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "cmstgm")
public class Cmstgm extends CommonProps{
    @Id
    private String storageId;
    private String storageNm;
    private String storageGb;
    private String storageOwnCd;
    private String storageType;
    private String zipCd;
    private String addr1;
    private String addr2;
    private String storeTel;
    private String mobileTel;
    private String ownerId;
    private String upStorageId;
    private String userNm;
    private String areaGb;
    private String delYn;
}
