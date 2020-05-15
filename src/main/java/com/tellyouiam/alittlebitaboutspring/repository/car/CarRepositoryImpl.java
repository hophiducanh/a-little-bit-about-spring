package com.tellyouiam.alittlebitaboutspring.repository.car;

import com.tellyouiam.alittlebitaboutspring.dto.car.CarRequest;
import com.tellyouiam.alittlebitaboutspring.entity.car.Car;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class CarRepositoryImpl implements CarRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<Car> getTotalCarsByModelEntityImpl(CarRequest request) {
		
		StoredProcedureQuery query = this.em.createStoredProcedureQuery("FIND_CARS_AFTER_YEAR", Car.class);
		query.registerStoredProcedureParameter("year", Integer.class, ParameterMode.IN);
		query.setParameter("year", request.getYear());
		query.execute();
		List<Car> cars = query.getResultList();
		return cars;
	}
}
