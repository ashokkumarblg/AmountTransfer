package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  @Override
  public ResponseEntity<String> transferMoney(Transfer transferBalance) {
	  String fromAccountNumber = transferBalance.getAccountFromId();
      String toAccountNumber = transferBalance.getAccountToId();
      BigDecimal amount = transferBalance.getAmount();
      
      if (fromAccountNumber.equals(toAccountNumber)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account id must be different.");
  	
      if ((amount.compareTo(BigDecimal.ZERO) <= 0)) {
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount must be greater than zero");
      }
      
      Account fromAccount = accounts.get(fromAccountNumber);
      Account toAccount = accounts.get(toAccountNumber);    
        
      if (fromAccount == null) { 
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Id "+fromAccountNumber+" is not exists! ");
      }
          
      if (toAccount == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Id "+toAccountNumber+" is not exists! ");
      }
      
      if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) < 0)) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount cannot be negative.");
      }
      
      if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0)) {
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount must be greater than zero");
      }
      
      if(fromAccount.getBalance().compareTo(amount) < 0) {
    	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
      } else {
    	  synchronized (this) {   		  	  
	    	  fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
	    	  fromAccount = accounts.putIfAbsent(fromAccount.getAccountId(), fromAccount);	    	  
	      	  
	    	  toAccount.setBalance(toAccount.getBalance().add(amount));
	    	  toAccount = accounts.putIfAbsent(toAccount.getAccountId(), toAccount); 
	    	      	      
    		  return ResponseEntity.status(HttpStatus.OK).body("Success: Amount "+amount+" transferred from "+fromAccount+" to "+toAccount+" .");    		  
    	  }    	  
      }
  }
}
