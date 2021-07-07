package io.spring.model.order.entity;

<<<<<<< HEAD
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

=======
import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.common.entity.CommonProps;
>>>>>>> 59a8621c1562d6aa02e547bbfab4aa92d84a3e8b
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<<<<<<< HEAD
@Entity
@Getter
@Setter
@Table(name = "tb_order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderMaster {

	@Id
	private String orderId;

	private String firstOrderId;
	private Date orderDate;
	private String orderStatus;
	private String channelGb;
	private long custId;
	private long deliId;
	private float orderAmt;
	private float receiptAmt;
	private String firstOrderGb;
	private String orderGb;
	private String channelOrderNo;
	private String custPcode;
	private String orderMemo;

	private Long regId;
	@CreationTimestamp
	private Date regDt;
	private Long updId;
	@UpdateTimestamp
	private Date updDt;

=======
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="tb_order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderMaster extends CommonProps {
    public TbOrderMaster(String orderId){
        this.orderId = orderId;
    }
    @Id
    private String orderId;
    private String firstOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date orderDate;
    private String orderStatus;
    private String channelGb;
    private Long custId;
    private Long deliId;
    private Float orderAmt;
    private Float receiptAmt;
    private String firstOrderGb;
    private String orderGb;
    private String channelOrderNo;
    private String custPcode;
    private String orderMemo;
>>>>>>> 59a8621c1562d6aa02e547bbfab4aa92d84a3e8b
}
