package com.SkillBox.users.service;

import com.SkillBox.users.entity.User;
import com.SkillBox.users.repository.GenderRepository;
import com.SkillBox.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;

    @Transactional
    public User saveUser(User user) {
        var gender = genderRepository.findOrCreateByDescription(user.getGender().getDescription());
        user.setGender(gender);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public User updateUserById(Long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return saveUser(user);
    }
}
