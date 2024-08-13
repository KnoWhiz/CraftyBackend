package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends MongoRepository<User, String> {
  User findByEmail(String email);

  boolean existsByEmail(String email);
}
