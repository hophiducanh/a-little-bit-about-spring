package com.tellyouiam.alittlebitaboutspring.repository.car;

import com.tellyouiam.alittlebitaboutspring.dto.car.CarRequest;
import com.tellyouiam.alittlebitaboutspring.entity.car.Car;

import java.util.List;

public interface CarRepositoryCustom {
	List<Car> getTotalCarsByModelEntityImpl(CarRequest request);
}
