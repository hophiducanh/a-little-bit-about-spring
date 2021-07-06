package com.tellyouiam.alittlebitaboutspring.entity.unrelatedentities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "multiple_recipes")
public class MultipleRecipe {
	@Id
	@Column(name = "cocktail")
	private Integer cocktail;
	
	@Column(name = "instructions")
	private String instructions;
}
