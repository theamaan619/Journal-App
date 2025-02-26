package com.Amaan.journalApp.service;

import com.Amaan.journalApp.entity.User;
import com.Amaan.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j // Logger Annotations
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /*
        slf4j  {simple logging facade(Abstarction) for java } is logging
        abstraction framework , it helps us to communicate with
        the underlying implementation
     */
    /*
        Creating Logger Instances but we can also use annotations
        private static final Logger logger = LoggerFactory.getLogger(UserService.class);
     */

    public void saveEntry(User user) {
        userRepository.save(user);
    }

    public boolean saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            /*
            When Instance is created of Logger Then this will be the Loggger.error method to print the error
            logger.error("Cannot Save the user it is Duplicated {}:",user.getUserName(),e);
             */
            // log.error("Cannot Save the user it is Duplicated {}:",user.getUserName(),e);
            log.error("Error");
            log.info("info");
            log.warn("Warning");
            log.debug("Debug");
            log.trace("Trace");
            return  false;
        }
    }
    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("ADMIN"));
        userRepository.save(user);
    }

    public List<User> geAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }
    public User findByUsername(String userName){
        return userRepository.findByUserName(userName);
    }
    public void deleteByUserName(String userName){
         userRepository.deleteByUserName(userName);
    }
}
