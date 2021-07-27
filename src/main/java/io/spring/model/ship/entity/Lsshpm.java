package io.spring.model.ship.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lsshpm")
public class Lsshpm extends CommonProps {
    // 이동지시 시 실행되는 생성자
    public Lsshpm(String shipId, Itasrt itasrt, TbOrderDetail tbOrderDetail, Ititmc ititmc){
        this.shipId = shipId;
        this.shipOrderGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipTimes = 1l;
        this.shipStatus = StringFactory.getGbOne(); // 01 출고지시, 04 출고.. 이동지시시 실행되는 생성자이므로 01 하드코딩
        this.deliId = null; // 이동지시 null, 출고지시 tb_order_master.deli_id
        this.shipItemCnt = null;
        this.receiptDt = new Date();
        this.storageId = itasrt.getStorageId();
        this.instructDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.applyDay = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.masterShipGb = StringFactory.getGbOne(); // 01 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0');
        this.delMethod = tbOrderDetail.getDeliMethod();
        this.rematGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipGb = StringFactory.getGbTwo(); // 02 하드코딩 (01 : 출고, 02 : 이동)
        this.itemGrade = ititmc.getItemGrade();
        this.deliCompanyCd = null;
        this.orderId = tbOrderDetail.getOrderId();
    }
    @Id
    private String shipId;
    private String shipOrderGb;
    private Long shipTimes;
    private String shipStatus;
    private Long deliId;
    private Long shipItemCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date receiptDt;
    private String storageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date instructDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date applyDay;
    private String masterShipGb;
    private String memo;
    private String siteGb;
    private String vendorId;
    private String delMethod;
    private String rematGb;
    private String shipGb;
    private String itemGrade;
    private String deliCompanyCd;
    private String orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date uploadDt;
    private String blNo;
}
