package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionDao extends MongoRepository<Subscription, String> {}
