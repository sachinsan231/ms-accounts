/**
 * 
 */
package com.example.accounts.controller;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.accounts.config.AccountsServiceConfig;
import com.example.accounts.model.Accounts;
import com.example.accounts.model.Cards;
import com.example.accounts.model.Customer;
import com.example.accounts.model.CustomerDetails;
import com.example.accounts.model.Loans;
import com.example.accounts.model.Properties;
import com.example.accounts.repository.AccountsRepository;
import com.example.accounts.service.client.CardsFeignClient;
import com.example.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;

/**
 * @author User
 *
 */
@RestController
public class AccountsController {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	@Autowired
	private AccountsServiceConfig accountsConfig;
	
	@Autowired
	private CardsFeignClient cardsFeignClient;
	
	@Autowired
	private LoansFeignClient loansFeignClient;

	@PostMapping("/myAccount")
	@Timed(value = "getAccountDetails.time", description = "time taken to return account details" )
	public Accounts getAccountDetails(@RequestBody Customer customer) {
		
		Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
		
		if(account != null) {
			return account;
		}
		
		return null;
	}
	
	
	@GetMapping("/account/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
				accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}
	
	@PostMapping("/myAccountDetails")
	//@CircuitBreaker(name = "defaultForCustomerSupportApp", fallbackMethod = "fallbackGetCustomerDetails") // this is with assumption that cards MS is fail
	@Retry(name = "retryForCustomerSupportApp", fallbackMethod = "fallbackGetCustomerDetails")
	public CustomerDetails getCustomerDetails(@RequestBody Customer customer) {
		logger.info("getCustomerDetails() started");
		Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
		if(account == null) {
			return null;
		}
		
		List<Loans> loansDetails = loansFeignClient.getLoansDetails(customer);
		List<Cards> cardDetails = cardsFeignClient.getCardDetails(customer);
		
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(account);
		customerDetails.setCards(cardDetails);
		customerDetails.setLoans(loansDetails);
		logger.info("getCustomerDetails() ended");
		return customerDetails;   
		
	}
	
	
	private CustomerDetails fallbackGetCustomerDetails(Customer customer, Throwable t) {
		
		System.err.println("error: "+t.getMessage());
		Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
		if(account == null) {
			return null;
		}
		
		List<Loans> loansDetails = loansFeignClient.getLoansDetails(customer);
		
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(account);
		customerDetails.setLoans(loansDetails);
		return customerDetails;   
		
	}
	
	@RateLimiter(name = "sayHello", fallbackMethod = "fallbackSayHello")
	@GetMapping("/sayHello")
	public String sayHello() {
		String hostname = System.getenv("HOSTNAME") == null ? "localhost" : System.getenv("HOSTNAME").toString();
		return "Hello, from testApp from cluster : "+hostname;
	}

	private String fallbackSayHello(Throwable t) {
		logger.error(" from fallbackSayHello : "+t.getMessage());
		return "Hi there, from fallbackSayHello";
	}
}
