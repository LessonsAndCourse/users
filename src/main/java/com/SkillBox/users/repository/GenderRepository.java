package com.SkillBox.users.repository;

import com.SkillBox.users.entity.Gender;
import org.springframework.data.repository.CrudRepository;

public interface GenderRepository extends CrudRepository<Gender, Long> {
}
