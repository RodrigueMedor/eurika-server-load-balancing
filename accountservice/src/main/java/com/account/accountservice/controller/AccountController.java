package com.account.accountservice.controller;

//import ch.qos.logback.core.net.ObjectWriter;
import com.account.accountservice.config.AccountsServiceConfig;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.account.accountservice.domain.Accounts;
import com.account.accountservice.domain.Customer;
import com.account.accountservice.dto.Properties;
import com.account.accountservice.dto.Cards;
import com.account.accountservice.dto.CustomerDetails;
import com.account.accountservice.dto.Loans;
import com.account.accountservice.repository.AccountsRepository;
import com.account.accountservice.service.client.CardsFeignClient;
import com.account.accountservice.service.client.LoansFeignClient;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    AccountsServiceConfig accountsConfig;

    @Autowired
    LoansFeignClient loansFeignClient;

    @Autowired
    CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    @Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
    public Accounts getAccountDetails(@RequestBody Customer customer) {

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        if (accounts != null) {
            return accounts;
        } else {
            return null;
        }

    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
                accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }


   // @CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod ="myCustomerDetailsFallBack")
  //  @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationid, @RequestBody Customer customer) {
        logger.info("myCustomerDetails() method started");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardsDetails(customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);
        logger.info("myCustomerDetails() method ended");
        return customerDetails;

    }

    @PostMapping("/myCustomerDetails")
    private CustomerDetails myCustomerDetailsFallBack(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardsDetails(customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setCards(cards);
        customerDetails.setLoans(loans);
        return customerDetails;

    }
}
