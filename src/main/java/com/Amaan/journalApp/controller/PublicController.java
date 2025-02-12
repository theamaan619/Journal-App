package com.Amaan.journalApp.controller;

import com.Amaan.journalApp.entity.User;
import com.Amaan.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @PostMapping("create-user")
    public void createUser(@RequestBody User user) {
        userService.saveNewUser(user);
    }
}
