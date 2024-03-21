package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/v1/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    AccountRepository repository;

    @GetMapping("")
    public ResponseEntity<List<Account>> getAll(){
        log.info("Received request to get all accounts");
        List<Account> accounts = repository.findAll();
        return ResponseEntity.ok().body(accounts);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteAll(){
        log.info("Received request to delete all accounts");
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        log.info("Received request to delete an account {}", id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
