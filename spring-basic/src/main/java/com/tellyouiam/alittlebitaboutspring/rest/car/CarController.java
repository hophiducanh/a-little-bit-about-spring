package com.tellyouiam.alittlebitaboutspring.rest.car;

import com.stackify.apm.Trace;
import com.tellyouiam.alittlebitaboutspring.dto.car.CarRequest;
import com.tellyouiam.alittlebitaboutspring.entity.car.Car;
import com.tellyouiam.alittlebitaboutspring.service.car.CarService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLContext;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api("Get car by model info")
public class CarController {
	@Autowired
	private CarService carService;
	
	@GetMapping(path = "/modelcount")
	@Trace
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
	@ApiOperation(value = "Returns list of cars after year[s].", response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden")
	})
	public List<Car> findCarsAfterYear(@ApiParam("The number of following year to find cars.") @RequestParam("year") @Min(1) @Max(10) Integer year) {
		return carService.findCarsAfterYear(year);
	}
	
	@GetMapping(path = "/carsByYear")
	public List<Car> findCarsByYear(CarRequest request) {
		return carService.getTotalCarsByModelEntityImpl(request);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		//JAVA SSL
		//https://stackoverflow.com/questions/10500511/how-to-find-what-ssl-tls-version-is-used-in-java
		
		//MYSQL SSL
		//https://dba.stackexchange.com/questions/36776/how-can-i-verify-im-using-ssl-to-connect-to-mysql
		System.out.println(String.join("\n", SSLContext.getDefault()
				.getSupportedSSLParameters()
				.getProtocols()));
	}
}
