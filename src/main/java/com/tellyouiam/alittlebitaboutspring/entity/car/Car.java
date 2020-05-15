package com.tellyouiam.alittlebitaboutspring.entity.car;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

@Getter
@Setter
@NoArgsConstructor
@NamedStoredProcedureQuery(name = "Car.getTotalCardsbyModelEntity", procedureName = "GET_TOTAL_CARS_BY_MODEL", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "model_in", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "count_out", type = Integer.class) })
@Entity
public class Car {
	//TODO
	//https://www.baeldung.com/hibernate-identifiers
	//https://www.baeldung.com/jpa-composite-primary-keys
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY) //default type is AUTO
	private Long id;
	
	// An interesting feature introduced in Hibernate 5 is the UUIDGenerator.
	// To use this, all we need to do is declare an id of type UUID with @GeneratedValue annotation:
	// private UUID id;
	
	@Column
	private String model;
	
	@Column
	private Integer year;
	
}
