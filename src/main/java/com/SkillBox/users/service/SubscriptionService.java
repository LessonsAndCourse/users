package com.SkillBox.users.service;

import com.SkillBox.users.entity.Subscription;
import com.SkillBox.users.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public Subscription createSubscription(Long userId1, Long userId2) {
        var existingSubscription = subscriptionRepository.findSubscriptionByUserId1AndUserId2(userId1, userId2);
        if (existingSubscription.isPresent()) {
            return existingSubscription.get();
        }
        Subscription subscription = new Subscription();
        subscription.setUserId1(userId1);
        subscription.setUserId2(userId2);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public List<Long> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId1(userId).stream()
                .map(Subscription::getUserId2)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubscription(Long userId1, Long userId2) {
        subscriptionRepository.findSubscriptionByUserId1AndUserId2(userId1, userId2)
                .ifPresent(subscriptionRepository::delete);
    }
}
