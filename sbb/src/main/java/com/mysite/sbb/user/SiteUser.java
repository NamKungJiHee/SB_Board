package com.mysite.sbb.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@SequenceGenerator(name="user_seq", sequenceName="user_seq", initialValue=1, allocationSize=1)
public class SiteUser {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_seq")
	private Long userId;
	
	@Column(unique=true)
	private String userName;
	
	private String password;
	
	@Column(unique=true)
	private String email;
	
	@Column
	private String profilePath;
	

}
