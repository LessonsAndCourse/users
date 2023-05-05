package com.SkillBox.users.controller;

import com.SkillBox.users.entity.User;
import com.SkillBox.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return usersService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        usersService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @GetMapping
    public List<User> findAllUsers(@RequestParam("isDeleted") Boolean isDeleted) {
        return usersService.findAllUsers(isDeleted);
    }

    @PutMapping("/{id}")
    public User updateUserById(@PathVariable Long id, @RequestBody User user) {
        return usersService.updateUserById(id, user);
    }
}
