package com.tellyouiam.alittlebitaboutspring.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class App {
	//https://dzone.com/articles/introduction-to-jpa-architecture?fromrel=true
	//https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa
	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PERSISTENCE");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		Student student = new Student("Ramesh", "Fadatare", "rameshfadatare@javaguides.com");
		entityManager.persist(student);
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}
}
