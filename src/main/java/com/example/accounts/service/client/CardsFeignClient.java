/**
 * 
 */
package com.example.accounts.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.accounts.model.Cards;
import com.example.accounts.model.Customer;

/**
 * @author User
 *
 */
@FeignClient(name = "cards")
public interface CardsFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json")
	List<Cards> getCardDetails(Customer customer);

}
