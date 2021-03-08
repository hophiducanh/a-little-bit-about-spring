package com.tellyouiam.alittlebitaboutspring.entity.unrelatedentities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
public class CockTail {
	
	@Id
	@Column(name = "cocktail_name")
	private Integer id;
	
	@Column
	private Double price;
	
	@OneToMany
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(
			name = "cocktail",
			referencedColumnName = "cocktail_name",
			insertable = false,
			updatable = false,
			foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private List<MultipleRecipe> recipeList;
}
