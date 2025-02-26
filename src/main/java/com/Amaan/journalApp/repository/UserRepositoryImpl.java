package com.Amaan.journalApp.repository;

import com.Amaan.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSA() {
        Query query = new Query();
        /*
            query.addCriteria(Criteria.where("email").exists(true));
            query.addCriteria(Criteria.where("email").ne(null).ne(""));
            For this we can write the regularexpression given below
        */
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z|A-Z]{2,6}$"));


        query.addCriteria(Criteria.where("sentimentAnalysis").exists(true));
        /*
       Criteria criteria = new Criteria();
       query.addCriteria(criteria.orOperator(
                Criteria.where("email").exists(true),
                Criteria.where("sentimentAnalysis").exists(true)) );
        */
        // here i am not including the username to which i don't want send anything ssss
        query.addCriteria(Criteria.where("userName").nin("jon","ahmed"));
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}
