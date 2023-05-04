package com.SkillBox.users.repository;

import com.SkillBox.users.entity.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    Optional<Subscription> findSubscriptionByUserId1AndUserId2(Long userId1, Long userId2);
    List<Subscription> findByUserId1(Long user1);
}
