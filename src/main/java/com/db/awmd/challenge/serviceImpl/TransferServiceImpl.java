package com.db.awmd.challenge.serviceImpl;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.TransferService;
import com.db.awmd.challenge.util.AccountsConstatnts;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

	@Autowired
	private AccountsRepository accountsRepository;
	
	public AccountsConstatnts accountsConstatnts;
	
	private ReentrantLock transfAcctLock;
	
	public TransferServiceImpl() {
		transfAcctLock = new ReentrantLock();
	}
	
	@Override
	public ResponseEntity<String> transferMoney(Transfer transferBalance) {
		log.info("Transfering amount {}", transferBalance);
		String fromAccountNumber = transferBalance.getAccountFromId();
		String toAccountNumber = transferBalance.getAccountToId();
		BigDecimal amount = transferBalance.getAmount();
		
		Account fromAccount = null;
		Account toAccount = null;
		
		if (fromAccountNumber.equals(toAccountNumber)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.DIFFERENT_ACCOUNTS);
		
		if ((amount.compareTo(BigDecimal.ZERO) <= 0)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.AMOUNT_GREATER_ZERO);
				
			
		fromAccount = accountsRepository.getAccount(fromAccountNumber);
		toAccount = accountsRepository.getAccount(toAccountNumber);  
			
		if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) < 0)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.AMOUNT_NOT_NEGATIVE);
		}

		if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.AMOUNT_GREATER_ZERO);
		}		
			
		if(fromAccount.getBalance().compareTo(amount) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.INSUFFICIENT_FUNDS);
		} else { 
			transfAcctLock.lock();
			try {
				fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
				fromAccount = accountsRepository.getAccount(fromAccount.getAccountId());    	  
				log.info("Transfering amount :: Debited from {} ", fromAccount.getAccountId());
				new EmailNotificationService().notifyAboutTransfer(fromAccount, "Amount of INR "+amount+" has been debited from "
						+ ""+fromAccount.getAccountId()+" to account Id, "+toAccount.getAccountId()+"");
				
				toAccount.setBalance(toAccount.getBalance().add(amount));
				toAccount = accountsRepository.getAccount(toAccount.getAccountId());
				log.info("Transfering amount :: Created to {}", toAccount.getAccountId());
				new EmailNotificationService().notifyAboutTransfer(toAccount, "Amount of INR "+amount+" has been created into "
						+ ""+toAccount.getAccountId()+" account Id, from "+fromAccount.getAccountId()+" account Id.");
		
			} finally {
				transfAcctLock.unlock();
			}			
		}
	
		return ResponseEntity.status(HttpStatus.OK).body("Success: Amount "+amount+" transferred from "+fromAccount+" to "+toAccount+" .");
	}
}