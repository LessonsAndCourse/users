package com.SkillBox.users.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {
    @GetMapping("/")
    public String index() {
        return "Hello world from users service!";
    }
}
