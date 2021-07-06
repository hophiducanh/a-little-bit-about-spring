package com.tellyouiam.alittlebitaboutspring.repository;

import com.sun.istack.NotNull;
import com.tellyouiam.alittlebitaboutspring.entity.unrelatedentities.CockTail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CockTailRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	public void test() {
		em.createQuery("select c from CockTail c join c.recipeList", CockTail.class).getSingleResult();
	}
}
