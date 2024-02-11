package com.bigtree.auth.service;

import com.bigtree.auth.entity.Contacts;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.repository.ContactsRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ContactsService {

    @Autowired
    ContactsRepository contactsRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Contacts create(Contacts contacts) {
        if ( StringUtils.isEmpty(contacts.getEmail())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is mandatory");
        }
        if ( StringUtils.isEmpty(contacts.getMobile())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mobile is mandatory");
        }
        if ( StringUtils.isEmpty(contacts.getAbout())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "About is mandatory");
        }
        if ( StringUtils.isEmpty(contacts.getMessage())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Message is mandatory");
        }
        log.info("Storing new contact from {}", contacts.getEmail());
        contacts.setDate(LocalDate.now());
         return contactsRepository.save(contacts);
    }

    public List<Contacts> lookup(String email, String mobile, String about, Boolean responded, LocalDate from, LocalDate to) {
        log.info("Finding contacts");
        Query query = new Query();
        if (StringUtils.isNotEmpty(email)) {
            query.addCriteria(Criteria.where("email").is(email));
        }
        if (StringUtils.isNotEmpty(mobile)) {
            query.addCriteria(Criteria.where("mobile").is(mobile));
        }
        if (StringUtils.isNotEmpty(about)) {
            query.addCriteria(Criteria.where("about").is(about));
        }
        if (responded != null) {
            query.addCriteria(Criteria.where("responded").is(responded));
        }
        if (from != null && to != null) {
            query.addCriteria(Criteria.where("date").gte(from).lte(to));
        } else if (from != null && to == null) {
            query.addCriteria(Criteria.where("date").gte(from));
        } else if (from == null && to != null) {
            query.addCriteria(Criteria.where("date").lte(to));
        }
        return mongoTemplate.find(query, Contacts.class);
    }

    public void delete(String email) {
        log.info("Delete all contacts from {}", email);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        final DeleteResult deleteResult = mongoTemplate.remove(query, "contacts");
        log.info("{} contacts deleted for {}", deleteResult.getDeletedCount(), email);
    }
}
