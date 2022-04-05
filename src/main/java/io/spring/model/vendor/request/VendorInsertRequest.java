package io.spring.model.vendor.request;

import lombok.Getter;
import lombok.Setter;

/**
 * vendor 등록용 DTO
 */
@Getter
@Setter
public class VendorInsertRequest
{
    private String id;
    private String vdNm;
    private String vdEnm;
    private String vendorType;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    private String delYn;
}
