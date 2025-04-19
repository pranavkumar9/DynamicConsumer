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

@RestController
@RequestMapping("/rmqapi/v1")
@CrossOrigin("*")
public class MainController {
	
	@Autowired
	private Publisher publisher;
	
	@Autowired
	private DynamicQueueService dynamicQueService;
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	@PostMapping("/addSchool")
	public ResponseEntity<String> addResturant(@RequestParam("name") String SName)
	{
		log.info("My scchool name" + SName);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@GetMapping("/publish")
	public ResponseEntity<String> sendMessage(@RequestParam("msg") String msg,@RequestParam("revId") String revId) throws Exception{
		log.info("Inside Controller");
		log.info("message in controller is: {}", msg);
		return new ResponseEntity<String>(publisher.sendStringMessage(msg,revId),HttpStatus.OK);
	}
	@PostMapping("/publish")
	public ResponseEntity<String> sendMessage(@RequestBody JsonMessage request){
	                                           
	    log.info("Received message: {}", request.getMessage());
	    return new ResponseEntity<>(publisher.sendObjectMessage(request), HttpStatus.OK);
	}
	
	@PostMapping("/reservation")
	public ResponseEntity<String> addReservation(@RequestParam("revId") String revId,@RequestParam("tableNo") String tableNo) throws Exception{
		log.info("Inside Controller");
		return new ResponseEntity<String>(dynamicQueService.addReservation(revId,tableNo),HttpStatus.OK);
	}
	
	@GetMapping("/deleteReservation")
	public ResponseEntity<String> deleteReservation(@RequestParam("revId") String revId) throws Exception{
		log.info("Inside Controller");
		return new ResponseEntity<String>(dynamicQueService.deleteReservation(revId),HttpStatus.OK);
	}
	
	

}
