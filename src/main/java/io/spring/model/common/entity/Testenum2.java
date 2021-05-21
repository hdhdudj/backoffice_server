package io.spring.model.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="testenum2")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
public class Testenum2 {
	

	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long seq;
	  
	  @Column(columnDefinition = "varchar(2) default '01'")
	  private String assortGb;
	  @Column(columnDefinition = "varchar(2) default '02'")
	  private String assortYn;
	  
	  private String brandId;

}



