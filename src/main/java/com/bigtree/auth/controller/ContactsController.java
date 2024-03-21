package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Contacts;
import com.bigtree.auth.service.ContactsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class ContactsController {

    @Autowired
    ContactsService contactsService;

    @GetMapping(value = "/v1/contacts")
    public ResponseEntity<List<Contacts>> getContacts(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "responded", required = false) Boolean responded,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to
    ) {
        log.info("Request get contacts");
        List<Contacts> contacts = contactsService.lookup(email, mobile, about, responded, from, to);
        log.info("Returning {} contacts", contacts.size());
        return ResponseEntity.ok(contacts);
    }

    @PostMapping(value = "/v1/contacts")
    public ResponseEntity<Contacts> create(@RequestBody Contacts req) {
        log.info("Creating new contacts from {}", req.getEmail());
        Contacts contacts = contactsService.create(req);
        if (contacts != null) {
            log.info("Created new contact from {}", contacts.getEmail());
        }
        return ResponseEntity.status(201).body(contacts);
    }

    @DeleteMapping(value = "/v1/contacts")
    public ResponseEntity<Void> delete(@RequestParam("email") String email) {
        log.info("Deleting contacts from {}", email);
        contactsService.delete(email);
        return ResponseEntity.accepted().build();
    }
}
