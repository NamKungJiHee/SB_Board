package com.mysite.sbb.lecture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Lecture {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer lectureId;
}
