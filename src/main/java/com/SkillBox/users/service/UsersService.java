package com.SkillBox.users.service;

import com.SkillBox.users.entity.User;
import com.SkillBox.users.repository.GenderRepository;
import com.SkillBox.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final EntityManager entityManager;

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

    @Transactional
    public List<User> findAllUsers(boolean isDeleted){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", isDeleted);
        Iterable<User> users =  userRepository.findAll();
        session.disableFilter("deletedUserFilter");
        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
    }
}
