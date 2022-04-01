package io.spring.model.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "suppliers")
public class Suppliers extends CommonProps{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long supplierId;
	private String supplierNm;
}
