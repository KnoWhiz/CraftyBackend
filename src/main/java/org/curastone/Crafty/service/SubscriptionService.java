package org.curastone.Crafty.service;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.SubscriptionDao;
import org.curastone.Crafty.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired private SubscriptionDao subscriptionDao;

    public Subscription createSubscription(Subscription subscription) {
        Optional<Subscription> existingSubscription =
                subscriptionDao.findByUserId(subscription.getUserId()).stream().findFirst();
        if (existingSubscription.isPresent()) {
            throw new IllegalArgumentException("User already has an active subscription.");
        }

        return subscriptionDao.save(subscription);
    }

    public List<Subscription> getSubscriptionsByUserId(ObjectId userId) {
        return subscriptionDao.findByUserId(userId);
    }
}
