package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transfer {	
	private String accountFromId;	
	private String accountToId;	
	private BigDecimal amount;
}
