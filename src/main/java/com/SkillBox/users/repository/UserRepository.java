package com.SkillBox.users.repository;

import com.SkillBox.users.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
