package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.Step;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepDao extends MongoRepository<Step, String> {}
