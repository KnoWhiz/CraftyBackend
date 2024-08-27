package org.curastone.Crafty.dao;

import java.util.List;
import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionDao extends MongoRepository<Subscription, ObjectId> {
    List<Subscription> findByUserId(ObjectId userId);
}
