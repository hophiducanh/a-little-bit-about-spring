package com.tellyouiam.alittlebitaboutspring.service.car;

import com.tellyouiam.alittlebitaboutspring.dto.car.CarRequest;
import com.tellyouiam.alittlebitaboutspring.entity.car.Car;
import com.tellyouiam.alittlebitaboutspring.repository.car.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
	@Autowired
	private CarRepository carRepository;
	
	public int getTotalCarsByModel(String model) {
		return carRepository.getTotalCarsByModel(model);
	}
	
	public int getTotalCarsByModelProcedureName(String model) {
		return carRepository.getTotalCarsByModelProcedureName(model);
	}
	
	public int getTotalCarsByModelValue(String model) {
		return carRepository.getTotalCarsByModelValue(model);
	}
	
	public int getTotalCarsByModelExplicit(String model) {
		return carRepository.GET_TOTAL_CARS_BY_MODEL(model);
	}
	
	public int getTotalCarsByModelEntity(String model) {
		return carRepository.getTotalCarsByModel(model);
	}
	
	public List<Car> findCarsAfterYear(Integer year) {
		return carRepository.findCarsAfterYear(year);
	}
	
	public List<Car> getTotalCarsByModelEntityImpl(CarRequest request) {
		return carRepository.getTotalCarsByModelEntityImpl(request);
	}
}
