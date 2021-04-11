package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {
	private MockMvc mockMvc; 
	
	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	  public void prepareMockMvc() {
		MockitoAnnotations.initMocks(this); // To use InjectMocks we use this line
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	}
	
	@Test
	public void transferMoney() throws Exception {
	
	  String fromAccountId = "Id-1" + System.currentTimeMillis();
	  Account account1 = new Account(fromAccountId, new BigDecimal("123.45"));
	  
	  String toAccountId = "Id-2" + System.currentTimeMillis(); 
	  Account account2 = new Account(toAccountId, new BigDecimal("456.78"));
	  
	  this.accountsService.createAccount(account1);
	  this.accountsService.createAccount(account2);
	  
	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	  	.content("{ \"accountFromId\":\""+fromAccountId+"\",\"accountToId\":\""+toAccountId+"\",\"amount\":100.00}"))
	  	.andExpect(status().isOk());
	}
	
	@Test
	public void transferSameAccount() throws Exception {
	   this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-123\",\"amount\":1000}")).andExpect(status().isBadRequest());
	}	 
	
	@Test
	public void transferAccountZeroBalance() throws Exception {
	   this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":0}")).andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferMoneyInsufficintBalance() throws Exception {
	
	  String fromAccountId = "Id-1" + System.currentTimeMillis();
	  Account account1 = new Account(fromAccountId, new BigDecimal("123.45"));
	  
	  String toAccountId = "Id-2" + System.currentTimeMillis(); 
	  Account account2 = new Account(toAccountId, new BigDecimal("456.78"));
	  
	  this.accountsService.createAccount(account1);
	  this.accountsService.createAccount(account2);
	  
	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	  	.content("{ \"accountFromId\":\""+fromAccountId+"\",\"accountToId\":\""+toAccountId+"\",\"amount\":4000.00}"))
	  	.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferMoneyZeroBalance() throws Exception {
	
	  String fromAccountId = "Id-1" + System.currentTimeMillis();
	  Account account1 = new Account(fromAccountId, new BigDecimal("0"));
	  
	  String toAccountId = "Id-2" + System.currentTimeMillis(); 
	  Account account2 = new Account(toAccountId, new BigDecimal("0"));
	  
	  this.accountsService.createAccount(account1);
	  this.accountsService.createAccount(account2);
	  
	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	  	.content("{ \"accountFromId\":\""+fromAccountId+"\",\"accountToId\":\""+toAccountId+"\",\"amount\":100}"))
	  	.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferMoneyNegativeBalance() throws Exception {
	
	  String fromAccountId = "Id-1" + System.currentTimeMillis();
	  Account account1 = new Account(fromAccountId, new BigDecimal("-100"));
	  
	  String toAccountId = "Id-2" + System.currentTimeMillis(); 
	  Account account2 = new Account(toAccountId, new BigDecimal("-10"));
	  
	  this.accountsService.createAccount(account1);
	  this.accountsService.createAccount(account2);
	  
	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	  	.content("{ \"accountFromId\":\""+fromAccountId+"\",\"accountToId\":\""+toAccountId+"\",\"amount\":100}"))
	  	.andExpect(status().isBadRequest());
	}
	
	
}
