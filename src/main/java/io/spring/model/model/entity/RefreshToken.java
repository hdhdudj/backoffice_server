package io.spring.model.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

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
