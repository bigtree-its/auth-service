package com.bigtree.auth.service;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.ActivateAccountRequest;
import com.bigtree.auth.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public void activateAccount(ActivateAccountRequest req) {
        log.info("Activating account {}", req.getAccountId());
        Optional<Account> accountOps = accountRepository.findById(req.getAccountId());
        if ( accountOps.isPresent()){
            Account account = accountOps.get();
            if (account.getActivationCode().equalsIgnoreCase(req.getActivationCode())){
                account.setActive(true);
                accountRepository.save(account);
                log.info("Account {} is activated", account.get_id());
            }else{
                throw new ApiException(HttpStatus.BAD_REQUEST, "Activation Code not found");
            }
        }else{
            throw new ApiException(HttpStatus.BAD_REQUEST, "Account not found");
        }
    }
}
