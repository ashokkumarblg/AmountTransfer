package com.db.awmd.challenge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.service.TransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class TransferController {
	
	private final TransferService transferService;
	
	@Autowired
	public TransferController(TransferService transferService) {
		this.transferService = transferService;
	}
	
	@PostMapping(path = "/transferMoney", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> transferMoney(@RequestBody Transfer transferBalance) {
		log.info("Transfering amount ... ");
		
		try {
			return this.transferService.transferMoney(transferBalance);
		} catch (Exception ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
