package com.Amaan.journalApp.service;

import com.Amaan.journalApp.api.response.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /*
     Yeh Generic Method hai means is mai hum entity denge
     toh usko ek type ka nahi rakh sakhte hai aur sab entity ka SuperClass
     object hota hai , isliye genric type liya hai
    */
    // Define a generic method that can return any type based on what is passed when calling the method
    public <T> T get(String key, Class<T> entityClass) {
        try {
            // 1. Retrieve the value from Redis using the given 'key'.
            // redisTemplate.opsForValue().get(key) fetches the object from the Redis cache.
            Object o = redisTemplate.opsForValue().get(key);
            // 2. Create an ObjectMapper instance.
            // ObjectMapper helps in converting the object from Redis (typically stored as JSON) into a Java object.
            ObjectMapper mapper = new ObjectMapper();

            // 3. Convert the object retrieved from Redis into the specified type 'entityClass'.
            // The toString() method is used to get the string representation of the object.
            // The mapper will then convert the string (JSON) into an object of type 'entityClass'.
            return mapper.readValue(o.toString(), entityClass);


        } catch (Exception e) {
            // 4. Handle any exceptions that occur during the process (e.g., Redis failure, JSON conversion failure).
            // If an exception occurs, log the error message and return null.
            log.error("Exception ", e);
            return null;
        }
    }

    public void set(String key, Object o, Long timeToLive) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, timeToLive, TimeUnit.SECONDS);
            log.info("Key Was Set in the Redis");
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }
}
