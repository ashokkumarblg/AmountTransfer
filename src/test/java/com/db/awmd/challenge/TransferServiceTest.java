package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {

	private MockMvc mockMvc;
  
	  @Autowired
	  private AccountsRepository accountsRepository;
	  
	  @Autowired
	  private WebApplicationContext webApplicationContext;
	  
	  @Before
	  public void prepareMockMvc() {
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	  }
	  	  
	  @Test
	  public void transferMoneyaccountIdNotExistsTest() throws Exception {
		  Account account = new Account("6543", new BigDecimal(2000));
		  
		  accountsRepository.createAccount(account);
		  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	      .content("{\"balance\":2000}")).andExpect(status().isBadRequest());
	  }
	  
	  @Test
	  public void createAccountNegativeBalance() throws Exception {
	    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
	      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
	  }
	  
	  @Test
	  public void createAccountZeroBalance() throws Exception {
	    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
	      .content("{\"accountId\":\"Id-123\",\"balance\":0}")).andExpect(status().isBadRequest());
	  }
}
