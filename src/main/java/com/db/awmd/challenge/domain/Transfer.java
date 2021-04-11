package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Transfer {	
	
	@NotNull
	@NotEmpty
	private String accountFromId;
	
	@NotNull
	@NotEmpty
	private String accountToId;	
	
	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	private BigDecimal amount;
		
	@JsonCreator
	public Transfer(@JsonProperty("accountFromId") String accountFromId, @JsonProperty("accountToId") String accountToId, @JsonProperty("amount") BigDecimal amount) {
	    this.accountFromId = accountFromId;
	    this.accountToId = accountToId;
	    this.amount = amount;
	  }
}
