package io.spring.model.ship.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lsshpm")
public class Lsshpm extends CommonProps {
    /**
     * 주문이동지시 저장, 출고 시 실행되는 생성자
     */
    public Lsshpm(String shipId, Itasrt itasrt, TbOrderDetail tbOrderDetail){
        this.shipId = shipId;
        this.shipOrderGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipTimes = 1l;
//        this.shipStatus = StringFactory.getGbOne(); // 01 출고지시, 04 출고.. 이동지시시 실행되는 생성자이므로 01 하드코딩
        this.deliId = null; // 이동지시 null, 출고지시 tb_order_master.deli_id
        this.shipItemCnt = null;
        this.receiptDt = LocalDateTime.now(); // 출고지시 일자
        this.storageId = itasrt.getStorageId();
        this.instructDt = new Date(); // 패킹일자 //Utilities.getStringToDate(StringFactory.getDoomDay());
        this.applyDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // 출고처리 일자
        this.masterShipGb = StringFactory.getGbOne(); // 01 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.delMethod = tbOrderDetail.getDeliMethod();
        this.rematGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipGb = StringFactory.getGbTwo(); // 02 하드코딩 (01 : 출고, 02 : 이동)
//        this.itemGrade = ititmc.getItemGrade(); //: 11로 고정. 이동지시와 출고에서는 11(정상품)만 다룸.
        this.deliCompanyCd = null;
        this.orderId = tbOrderDetail.getOrderId();

        this.shipGb = StringFactory.getGbThree(); // 01:일반출고 03:주문이동지시 04:상품이동지시
        this.masterShipGb = StringFactory.getGbThree(); // 01:일반출고 03:주문이동지시 04:상품이동지시
    }

    /**
     * 상품이동지시 저장시 실행되는 생성자
     */
    public Lsshpm(String shipId){
        this.shipId = shipId;
        this.shipOrderGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipTimes = 1l;
        this.shipStatus = StringFactory.getGbOne(); // 01 : 출고지시, 04 : 출고 (04 하드코딩)
        this.deliId = null; // 이동지시 : null, 출고지시 : tbOrderDetail.deliId
        this.shipItemCnt = null;
        this.receiptDt = LocalDateTime.now();
//        this.storageId : 바깥에서 set
        this.instructDt = new Date();//Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.applyDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // 9999-12-31 하드코딩
        this.masterShipGb = StringFactory.getGbOne(); // 01 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
//        this.delMethod : 바깥에서 set
        this.rematGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipGb = StringFactory.getGbTwo(); // 02 하드코딩 (01 : 출고, 02 : 이동)
//        this.itemGrade : 11로 고정. 해당 객체에서는 정상품만 다룸.
        this.deliCompanyCd = null;

        this.shipGb = StringFactory.getGbFour(); // 01:일반출고 03:주문이동지시 04:상품이동지시
        this.masterShipGb = StringFactory.getGbFour(); // 01:일반출고 03:주문이동지시 04:상품이동지시
    }

    /**
     * 출고 - 출고지시 : 출고지시 저장시 실행되는 생성자
     */
    public Lsshpm(String shipId, ShipIndicateSaveListData shipIndicateSaveDataList) {
        this.shipId = shipId;
//        this.shipOrderGb = shipIndicateSaveDataList.
    }

    @Id
    private String shipId;
    private String shipOrderGb;
    private Long shipTimes;
    private String shipStatus;
    private Long deliId;
    private Long shipItemCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime receiptDt;
    private String storageId;
    private String oStorageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date instructDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime applyDay;
    private String masterShipGb;
    private String memo;
    private String siteGb;
    private String vendorId;
    private String delMethod;
    private String rematGb;
    private String shipGb;
    private String itemGrade = StringFactory.getStrEleven(); // 11 하드코딩
    private String deliCompanyCd;
    private String orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date uploadDt;
    private String blNo;

    // 연관관계 : Lsshpd
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Lsshpd.class)
    @JoinColumn(name = "shipId", referencedColumnName = "shipId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private List<Lsshpd> lsshpdList;

    // 연관관계 : TbOrderMaster
    @OneToOne(fetch = FetchType.LAZY, targetEntity = TbOrderMaster.class)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private TbOrderMaster tbOrderMaster;
}
