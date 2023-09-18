package com.logbasex.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoFluxTest {
	
	@Test
	public void testMono() {
		//Mono is publisher implements CorePublisher
		Mono<String> monoString = Mono.just("logbasex");
		
		//invoke subscriber
		monoString.subscribe(System.out::println);
	}
	
	/**
	 * 17:05:33.251 [Test worker] DEBUG reactor.util.Loggers - Using Slf4j logging framework
	 * 17:05:33.266 [Test worker] INFO reactor.Mono.Just.1 - | onSubscribe([Synchronous Fuseable] Operators
	 * .ScalarSubscription)
	 * 17:05:33.269 [Test worker] INFO reactor.Mono.Just.1 - | request(unbounded)
	 * 17:05:33.270 [Test worker] INFO reactor.Mono.Just.1 - | onNext(logbasex)
	 * logbasex
	 * 17:05:33.272 [Test worker] INFO reactor.Mono.Just.1 - | onComplete()
	 */
	@Test
	public void testMonoOnLog() {
		//Mono is publisher implements CorePublisher
		Mono<String> monoString = Mono.just("logbasex").log();
		
		//invoke subscriber
		monoString.subscribe(System.out::println);
	}
	
	/**
	 * 17:06:05.694 [Test worker] INFO reactor.Mono.IgnoreThen.1 - onSubscribe(MonoIgnoreThen.ThenIgnoreMain)
	 * 17:06:05.696 [Test worker] INFO reactor.Mono.IgnoreThen.1 - request(unbounded)
	 * 17:06:05.699 [Test worker] ERROR reactor.Mono.IgnoreThen.1 - onError(java.lang.RuntimeException: Exception
	 * Occurred)
	 * 17:06:05.702 [Test worker] ERROR reactor.Mono.IgnoreThen.1 -
	 * java.lang.RuntimeException: Exception Occurred
	 * 	    at com.logbasex.webflux.MonoFluxTest.testMonoOnError(MonoFluxTest.java:42)
	 * Exception Occurred
	 */
	@Test
	public void testMonoOnError() {
		//Mono is publisher implements CorePublisher
		Mono<?> monoString = Mono.just("logbasex")
				.then(Mono.error(new RuntimeException("Exception Occurred")))
				.log();
		
		//invoke subscriber
		monoString.subscribe(System.out::println, e -> System.out.println(e.getMessage()));
		System.out.println("Error does not terminate the program");
	}
	
	/**
	 * 17:04:54.527 [Test worker] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray
	 * .ArraySubscription)
	 * 17:04:54.529 [Test worker] INFO reactor.Flux.Array.1 - | request(unbounded)
	 * 17:04:54.530 [Test worker] INFO reactor.Flux.Array.1 - | onNext(Spring)
	 * Spring
	 * 17:04:54.530 [Test worker] INFO reactor.Flux.Array.1 - | onNext(Spring Boot)
	 * Spring Boot
	 * 17:04:54.530 [Test worker] INFO reactor.Flux.Array.1 - | onNext(Me)
	 * Me
	 * 17:04:54.531 [Test worker] INFO reactor.Flux.Array.1 - | onNext(Webflux)
	 * Webflux
	 * 17:04:54.531 [Test worker] INFO reactor.Flux.Array.1 - | onComplete()
	 */
	@Test
	public void testFlux() {
		Flux<String> fluxValues = Flux
				.just("Spring", "Spring Boot", "Me", "Webflux")
				.log();
		
		fluxValues.subscribe(System.out::println);
	}
	
	/**
	 * 17:14:55.222 [Test worker] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
	 * 17:14:55.225 [Test worker] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
	 * 17:14:55.225 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(Spring)
	 * Spring
	 * 17:14:55.225 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(Spring Boot)
	 * Spring Boot
	 * 17:14:55.226 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(Me)
	 * Me
	 * 17:14:55.226 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(Webflux)
	 * Webflux
	 * 17:14:55.227 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(AWS)
	 * AWS
	 * 17:14:55.228 [Test worker] ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception
	 * occurred in Flux)
	 * 17:14:55.231 [Test worker] ERROR reactor.Flux.ConcatArray.1 -
	 * java.lang.RuntimeException: Exception occurred in Flux
	 *      at com.logbasex.webflux.MonoFluxTest.testFluxOnError(MonoFluxTest.java:85)
	 *      ...
	 * 17:14:55.240 [Test worker] ERROR reactor.core.publisher.Operators - Operator called default onErrorDropped
	 * reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.RuntimeException: Exception occurred in Flux
	 * Caused by: java.lang.RuntimeException: Exception occurred in Flux
	 *      at com.logbasex.webflux.MonoFluxTest.testFluxOnError(MonoFluxTest.java:85)
	 *      ...
	 */
	@Test
	public void testFluxOnError() {
		Flux<String> fluxValues = Flux
				.just("Spring", "Spring Boot", "Me", "Webflux")
				.concatWithValues("AWS")
				.concatWith(Flux.error(new RuntimeException("Exception occurred in Flux")))
				.log();
		
		fluxValues.subscribe(System.out::println);
	}
}
