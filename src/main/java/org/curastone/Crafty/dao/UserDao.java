package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.UserRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends MongoRepository<UserRegistration, Integer> {

  // Optional<User> findById(ObjectId userId);

  // Optional<User> findByProviderUserId(String providerUserId);
}
