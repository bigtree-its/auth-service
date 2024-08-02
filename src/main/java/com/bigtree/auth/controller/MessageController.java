package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Message;
import com.bigtree.auth.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/messages/v1")
public class MessageController {

    @Autowired
    MessageService messageService;

    @GetMapping(value = "")
    public ResponseEntity<List<Message>> getContacts(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "responded", required = false) Boolean responded,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to
    ) {
        log.info("Request to get messages");
        List<Message> contacts = messageService.lookup(email, mobile, about, responded, from, to);
        log.info("Returning {} messages", contacts.size());
        return ResponseEntity.ok(contacts);
    }

    @PostMapping(value = "")
    public ResponseEntity<Message> create(@RequestBody Message req) {
        log.info("Creating new messages from {}", req.getEmail());
        Message message = messageService.create(req);
        if (message != null) {
            log.info("Created new messages from {}", message.getEmail());
        }
        return ResponseEntity.status(201).body(message);
    }

    @DeleteMapping(value = "")
    public ResponseEntity<Void> delete(@RequestParam("email") String email) {
        log.info("Deleting messages from {}", email);
        messageService.delete(email);
        return ResponseEntity.accepted().build();
    }
}
