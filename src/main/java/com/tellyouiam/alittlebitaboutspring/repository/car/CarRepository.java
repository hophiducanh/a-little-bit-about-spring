package com.tellyouiam.alittlebitaboutspring.repository.car;

import com.tellyouiam.alittlebitaboutspring.dto.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
	@Procedure
	int GET_TOTAL_CARS_BY_MODEL(String model);
	
	@Procedure("GET_TOTAL_CARS_BY_MODEL")
	int getTotalCarsByModel(String model);
	
	@Procedure(procedureName = "GET_TOTAL_CARS_BY_MODEL")
	int getTotalCarsByModelProcedureName(String model);
	
	@Procedure(value = "GET_TOTAL_CARS_BY_MODEL")
	int getTotalCarsByModelValue(String model);
	
	@Procedure(name = "Car.getTotalCardsbyModelEntity")
	int getTotalCarsByModelEntiy(@Param("model_in") String model);
	
	//https://www.baeldung.com/spring-data-jpa-stored-procedures
	@Query(value = "CALL FIND_CARS_AFTER_YEAR(:year_in);", nativeQuery = true)
	List<Car> findCarsAfterYear(@Param("year_in") Integer year_in);
}
