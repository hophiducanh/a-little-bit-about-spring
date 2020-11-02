package com.tellyouiam.alittlebitaboutspring.rest.car;

import com.tellyouiam.alittlebitaboutspring.dto.car.CarRequest;
import com.tellyouiam.alittlebitaboutspring.entity.car.Car;
import com.tellyouiam.alittlebitaboutspring.service.car.CarService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
public class CarController {
	@Autowired
	private CarService carService;
	
	@GetMapping(path = "/modelcount")
	public long getTotalCarsByModel(@RequestParam("model") String model) {
		return carService.getTotalCarsByModel(model);
	}
	
	@GetMapping(path = "/modelcountP")
	public long getTotalCarsByModelProcedureName(@RequestParam("model") String model) {
		return carService.getTotalCarsByModelProcedureName(model);
	}
	
	@GetMapping(path = "/modelcountV")
	public long getTotalCarsByModelVaue(@RequestParam("model") String model) {
		return carService.getTotalCarsByModelValue(model);
	}
	
	@GetMapping(path = "/modelcountEx")
	public long getTotalCarsByModelExplicit(@RequestParam("model") String model) {
		return carService.getTotalCarsByModelExplicit(model);
	}
	
	@GetMapping(path = "/modelcountEn")
	public long getTotalCarsByModelEntity(@RequestParam("model") String model) {
		return carService.getTotalCarsByModelEntity(model);
	}
	
	@GetMapping(path = "/carsafteryear")
	@ApiOperation("Returns list of cars after year[s].")
	public List<Car> findCarsAfterYear(@ApiParam("The number of following year to find cars.") @RequestParam("year") @Min(1) @Max(10) Integer year) {
		return carService.findCarsAfterYear(year);
	}
	
	@GetMapping(path = "/carsByYear")
	public List<Car> findCarsByYear(CarRequest request) {
		return carService.getTotalCarsByModelEntityImpl(request);
	}
}
