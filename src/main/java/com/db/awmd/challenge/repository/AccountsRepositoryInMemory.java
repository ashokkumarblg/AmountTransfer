package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.util.AccountsConstatnts;

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
  public ResponseEntity<String> updateAccount(Account account) {
	  Account currentAccount = null;
	  String acctId = account.getAccountId();
	  BigDecimal amount = account.getBalance();
	  
	  if (amount.compareTo(BigDecimal.ZERO) <= 0) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AccountsConstatnts.AMOUNT_GREATER_ZERO);
	  }
	  currentAccount = getAccount(acctId);
	  if(currentAccount != null) {
		  currentAccount.setBalance(currentAccount.getBalance().add(account.getBalance()));			  
	  } else {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account id " + account.getAccountId() + " does not exists!");
	  }
	  
	  return ResponseEntity.status(HttpStatus.OK).body("Account Id {"+currentAccount.getAccountId()+"} updated successfully. Current available balance is, ["+currentAccount.getBalance()+"]");
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }
}
