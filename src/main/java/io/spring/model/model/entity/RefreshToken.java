package io.spring.model.model.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "refreshtoken")
@Getter
@Setter
@ToString
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String token;

	private Instant expiryDate;

	private String userId;

	@OneToOne
	@JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	private User user;


}
