package com.db.awmd.challenge.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Transfer;

@Service
public interface TransferService {

	public ResponseEntity<String> transferMoney(Transfer transferBalance);
		
}
