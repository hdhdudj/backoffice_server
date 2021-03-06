package io.spring.model.purchase.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.enums.DirectOrImport;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.ship.entity.Lsshpm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name="lspchm")
@Getter
@Setter
@ToString
public class Lspchm extends CommonProps {
    public Lspchm(){}
    public Lspchm(Lspchm purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
		this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus(); // 01 : 발주, 03 : 부분입고, 04 : 완전입고, 05 : 취소
        this.purchaseRemark = purchaseInsertRequestData.getPurchaseRemark();
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
		this.vendorId = purchaseInsertRequestData.getVendorId(); // "0001" 하드코딩
        this.siteOrderNo = purchaseInsertRequestData.getSiteOrderNo();
        this.siteTrackNo = purchaseInsertRequestData.getSiteTrackNo();
        this.localPrice = purchaseInsertRequestData.getLocalPrice();
        this.newLocalPrice = this.localPrice;
        this.localDeliFee = purchaseInsertRequestData.getLocalDeliFee();
        this.newLocalDeliFee = this.localDeliFee;
        this.localTax = purchaseInsertRequestData.getLocalTax();
        this.newLocalTax = this.localTax;
        this.disPrice = purchaseInsertRequestData.getDisPrice();
        this.newDisPrice = this.disPrice;
        // this.purchaseGb = StringFactory.getGbOne(); // "01" : 일반발주
        this.purchaseGb = purchaseInsertRequestData.getPurchaseGb();
        this.ownerId = purchaseInsertRequestData.getOwnerId();
        this.storeCd = purchaseInsertRequestData.getStoreCd(); // "00001"
        this.oStoreCd = purchaseInsertRequestData.getOStoreCd();
        this.terms = purchaseInsertRequestData.getTerms();
        this.delivery = purchaseInsertRequestData.getDelivery();
        this.payment = purchaseInsertRequestData.getPayment();
        this.carrier = purchaseInsertRequestData.getCarrier();

        this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();
    }
    public Lspchm(PurchaseInsertRequestData purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus(); // 01 : 발주, 05 : 취소
        this.purchaseRemark = purchaseInsertRequestData.getPurchaseRemark();
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
		this.vendorId = purchaseInsertRequestData.getVendorId();
        this.siteOrderNo = purchaseInsertRequestData.getSiteOrderNo();
        this.siteTrackNo = purchaseInsertRequestData.getSiteTrackNo();
        this.localPrice = purchaseInsertRequestData.getLocalPrice();
        this.newLocalPrice = this.localPrice;
        this.localDeliFee = purchaseInsertRequestData.getLocalDeliFee();
        this.newLocalDeliFee = this.localDeliFee;
        this.localTax = purchaseInsertRequestData.getLocalTax();
        this.newLocalTax = this.localTax;
        this.disPrice = purchaseInsertRequestData.getDisPrice();
        this.newDisPrice = this.disPrice;
		// this.purchaseGb = StringFactory.getGbOne(); // "01" : 일반발주
		this.purchaseGb = purchaseInsertRequestData.getPurchaseGb();
        this.ownerId = purchaseInsertRequestData.getOwnerId();
        this.storeCd = purchaseInsertRequestData.getStorageId(); // 바깥에서 set
//        this.oStoreCd = purchaseInsertRequestData.getOStorageId();
        this.terms = purchaseInsertRequestData.getTerms();
        this.delivery = purchaseInsertRequestData.getDelivery();
        this.payment = purchaseInsertRequestData.getPayment();
        this.carrier = purchaseInsertRequestData.getCarrier();

		this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();
        this.piNo = purchaseInsertRequestData.getPiNo();
        this.memo = purchaseInsertRequestData.getMemo();

		this.setRegId(purchaseInsertRequestData.getUserId());
        this.setUpdId(purchaseInsertRequestData.getUserId());
    }

    /**
     * 주문이동지시 저장시 실행되는 생성자
     */
	public Lspchm(String orderGoodsType, String purchaseNo) {
        this.purchaseNo = purchaseNo;
        this.purchaseDt = LocalDateTime.now();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
		this.purchaseStatus = StringFactory.getGbOne(); // 01 : 발주, 04 : 이동지시?, 05 : 취소 (04 하드코딩)
//        this.purchaseRemark : 바깥 set
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
		this.vendorId = "AAAAAA"; // todo : 아직 방식이 정해지지 않았음. (원건의 구매처를 넣을 수도 있고.. 임시 하드코딩)
//        this.siteOrderNo : 바깥 set
//        this.siteTrackNo : 바깥 set (?)
//        this.localPrice : ?
        this.newLocalPrice = this.localPrice;
//        this.localDeliFee : ?
        this.newLocalDeliFee = this.localDeliFee;
//        this.localTax : ?
//        this.disPrice : ?
        this.newDisPrice = this.disPrice;

		if (orderGoodsType.equals("01")) {
			// 주문이동지시
			this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청 (02 하드코딩)
			this.dealtypeCd = "01";
		} else if (orderGoodsType.equals("02")) {
			// 상품이동지시
			this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청 (02 하드코딩)
			this.dealtypeCd = "02";
		} else if (orderGoodsType.equals("03")) {
			// 상품이동지시
			this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청 (02 하드코딩)
			this.dealtypeCd = "03";
		}

//        this.purchaseVendorId : ?
//        this.storeCd : 바깥 set
//        this.oStoreCd : 바깥 set (itasrt의 창고id)
//        this.terms : ?
//        this.delivery : ?
//        this.payment : ?
//        this.carrier : ?


    }

    /**
     * 상품이동지시 저장시 실행되는 생성자
     */
    public Lspchm(String purchaseNo, Lsshpm lsshpm, String regId) {
        this.purchaseNo = purchaseNo;
        this.purchaseDt = LocalDateTime.now();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
        this.purchaseStatus = StringFactory.getGbOne(); // 01:발주 03:부분입고 04:완전입고 05:취소  A1:송금완료 A2:거래처선금입금 A3:거래처잔금입금 (01 하드코딩)
//        this.purchaseRemark : 바깥 set
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
		this.vendorId = "AAAAAA"; // StringFactory.getFourStartCd(); // "0001" 하드코딩
//        this.siteOrderNo : 바깥 set
//        this.siteTrackNo : 바깥 set (?)
//        this.localPrice : ?
        this.newLocalPrice = this.localPrice;
//        this.localDeliFee : ?
        this.newLocalDeliFee = this.localDeliFee;
//        this.localTax : ?
//        this.disPrice : ?
        this.newDisPrice = this.disPrice;
        this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청 (02 하드코딩)
        this.storeCd = lsshpm.getOStorageId();
        this.oStoreCd = null;//lsshpm.getOStorageId();
//        this.purchaseVendorId : ?
//        this.terms : ?
//        this.delivery : ?
//        this.payment : ?
//        this.carrier : ?
        super.setRegId(regId);
        super.setUpdId(regId);
//        this.dealtypeCd = StringFactory.getGbOne(); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (01 하드코딩) 바깥에서 set
    }

    /**
     *  입고예정재고가 있을 때 그에 따른 발주 데이터를 만드는 생성자
     */
    public Lspchm(TbOrderDetail tbOrderDetail, DirectOrImport di) {
        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        this.purchaseDt = LocalDateTime.now();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
        this.purchaseStatus = StringFactory.getGbOne(); // 01 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩 (고도몰)
        this.vendorId = itasrt.getVendorId();
        this.dealtypeCd = StringFactory.getGbThree(); // 03 (입고예정주문발주) 하드코딩
        this.siteOrderNo = tbOrderDetail.getChannelOrderNo();
        if(di.equals(DirectOrImport.direct)){ // 직구
            this.purchaseGb = di.getFieldName(); // 01 (일반발주)
        }
        else if(di.equals(DirectOrImport.purchase)) { // 수입, 일반발주
            this.purchaseGb = di.getFieldName(); // 01 (일반발주)
        }
        else if(di.equals(DirectOrImport.move)){ // 수입, 이동요청
            this.purchaseGb = di.getFieldName(); // 02 (이동요청)
        }
        this.storeCd = tbOrderDetail.getStorageId();
    }

    @Id
    private String purchaseNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime purchaseDt;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
    private String purchaseStatus;
    private String purchaseRemark;
    private String siteGb;
    private String vendorId;
    private String dealtypeCd;
    private String siteOrderNo;
    private String siteTrackNo;
    private String purchaseCustNm;
    private Float localPrice;
    private Float newLocalPrice;
    private Float localDeliFee;
    private Float newLocalDeliFee;
    private Float localTax;
    private Float newLocalTax;
    private Float disPrice;
    private Float newDisPrice;
    private String cardId;
    private String purchaseGb;
//    private String purchaseVendorId; : ownerId로 변경됨
    private String ownerId;
    private String affilVdId;
    private String storeCd;
    private String oStoreCd;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    // 21-11-11 추가된 컬럼
    private LocalDateTime printDt;
    private String purchaseEmail;
    // 21-12-02 추가된 컬럼
    private String piNo;
    private String memo;
    // 22-01-06 추가된 컬럼
    private Float deliFee;

    // 연관관계 : lspchd
    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "purchaseNo", referencedColumnName = "purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private List<Lspchd> lspchdList;
}
