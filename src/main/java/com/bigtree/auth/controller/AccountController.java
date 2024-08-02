package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.ActivateAccountRequest;
import com.bigtree.auth.model.ApiResponse;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/accounts/v1")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("")
    public ResponseEntity<List<Account>> getAll(){
        log.info("Received request to get all accounts");
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok().body(accounts);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteAll(){
        log.info("Received request to delete all accounts");
        accountRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        log.info("Received request to delete an account {}", id);
        accountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/activate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> activateAccount(@Valid @RequestBody ActivateAccountRequest req){
        log.info("Received request to activate account {}", req.getAccountId());
        accountService.activateAccount(req);
        ApiResponse apiResponse = ApiResponse.builder().message("Your account has been successfully activated.").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
