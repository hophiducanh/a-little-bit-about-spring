package com.logbasex.webflux.controller;

import com.logbasex.webflux.dto.Customer;
import com.logbasex.webflux.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customerSv;
	
	@GetMapping("/customers")
	public List<Customer> getAllCustomers() {
		return customerSv.loadAlLCustomers();
	}
	
	/**
	 * text/event-stream is Server-Sent Events
	 * <p>
	 * Server-Sent Events (SSE):
	 * Server-Sent Events is a web technology that allows a web server to send real-time updates to a web browser over
	 * a single HTTP connection. It's a simple and efficient way to enable server-to-client communication. In SSE, the
	 * server pushes data to the client as text-based events, typically in the form of plain text or JSON.
	 * <p>
	 * Key features of SSE:
	 * - Data is pushed from the server to the client without the client needing to make repeated requests (like in
	 * polling).
	 * - SSE uses a simple event-driven API on the client side, allowing you to listen for specific events sent by the
	 * server.
	 * - It is typically unidirectional, with data flowing from server to client.
	 */
	@GetMapping(value = "/customers/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Customer> getAllCustomersStream() {
		return customerSv.loadAlLCustomersStream();
	}
}
