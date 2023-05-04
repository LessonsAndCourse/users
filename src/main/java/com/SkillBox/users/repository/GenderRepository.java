package com.SkillBox.users.repository;

import com.SkillBox.users.entity.Gender;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface GenderRepository extends CrudRepository<Gender, Long> {

    default Gender findOrCreateByDescription(String description) {
        return findByDescription(description)
                .orElseGet(() -> save(new Gender(description)));
    }

    Optional<Gender> findByDescription(String description);
}
