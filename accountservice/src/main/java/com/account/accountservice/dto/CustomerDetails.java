/**
 * 
 */
package com.account.accountservice.dto;

import com.account.accountservice.domain.Accounts;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author EazyBytes
 *
 */
@Getter
@Setter
@ToString
public class CustomerDetails {
	
	private Accounts accounts;
	private List<Loans> loans;
	private List<Cards> cards;
	
	

}
