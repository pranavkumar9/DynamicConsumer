package com.pks.RMQ_main_project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pks.RMQ_main_project.dynamicQ.DynamicQueueService;
import com.pks.RMQ_main_project.publisher.Publisher;

import entity.JsonMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/rmqapi/v1")
@CrossOrigin("*")
@Tag(name = "RabbitMQ API", description = "Endpoints for RabbitMQ messaging operations")
public class MainController {
	
	@Autowired
	private Publisher publisher;
	
	@Autowired
	private DynamicQueueService dynamicQueService;
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	
	
	@Operation(summary = "Add a school", description = "Adds a new school with the specified name")
	@PostMapping("/addSchool")
	public ResponseEntity<String> addResturant(
			@Parameter(description = "School name") @RequestParam("name") String SName)
	{
		log.info("My scchool name" + SName);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@Operation(summary = "Publish a message", description = "Publishes a string message to a specific recipient")
	@GetMapping("/publish")
	public ResponseEntity<String> sendMessage(
			@Parameter(description = "Message content") @RequestParam("msg") String msg,
			@Parameter(description = "Reservation ID") @RequestParam("revId") String revId) throws Exception{
		log.info("Inside Controller");
		log.info("message in controller is: {}", msg);
		return new ResponseEntity<String>(publisher.sendStringMessage(msg,revId),HttpStatus.OK);
	}
	
	@Operation(summary = "Publish JSON message", description = "Publishes a JSON message to RabbitMQ")
	@PostMapping("/publish")
	public ResponseEntity<String> sendMessage(
			@Parameter(description = "JSON message object", required = true) @RequestBody JsonMessage request){
	                                           
	    log.info("Received message: {}", request.getMessage());
	    return new ResponseEntity<>(publisher.sendObjectMessage(request), HttpStatus.OK);
	}
	
	@Operation(summary = "Add reservation", description = "Creates a new reservation with specified ID and table number")
	@PostMapping("/reservation")
	public ResponseEntity<String> addReservation(
			@Parameter(description = "Reservation ID") @RequestParam("revId") String revId,
			@Parameter(description = "Table number") @RequestParam("tableNo") String tableNo) throws Exception{
		log.info("Inside Controller");
		return new ResponseEntity<String>(dynamicQueService.addReservation(revId,tableNo),HttpStatus.OK);
	}
	
	
	@Operation(summary = "Delete reservation", description = "Removes an existing reservation by ID")
	@GetMapping("/deleteReservation")
	public ResponseEntity<String> deleteReservation(
			@Parameter(description = "Reservation ID") @RequestParam("revId") String revId) throws Exception{
		log.info("Inside Controller");
		return new ResponseEntity<String>(dynamicQueService.deleteReservation(revId),HttpStatus.OK);
	}
	
	

}
