package com.example.accounts.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.accounts.model.Accounts;

/**
 * 
 * @author User
 *
 */
public interface AccountsRepository extends CrudRepository<Accounts, Long>{

	Accounts findByCustomerId(int customerId);
}
