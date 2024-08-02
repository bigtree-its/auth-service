package com.bigtree.auth.service;

import com.bigtree.auth.entity.Message;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.repository.MessageRepository;
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
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Message create(Message message) {
        if ( StringUtils.isEmpty(message.getEmail())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is mandatory");
        }
        if ( StringUtils.isEmpty(message.getMobile())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mobile is mandatory");
        }
        if ( StringUtils.isEmpty(message.getAbout())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "About is mandatory");
        }
        if ( StringUtils.isEmpty(message.getContent())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Message Content is mandatory");
        }
        log.info("Storing new message from {}", message.getEmail());
        message.setDate(LocalDate.now());
         return messageRepository.save(message);
    }

    public List<Message> lookup(String email, String mobile, String about, Boolean responded, LocalDate from, LocalDate to) {
        log.info("Finding messages");
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
        return mongoTemplate.find(query, Message.class);
    }

    public void delete(String email) {
        log.info("Delete all message from {}", email);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        final DeleteResult deleteResult = mongoTemplate.remove(query, "message");
        log.info("{} message deleted for {}", deleteResult.getDeletedCount(), email);
    }
}
