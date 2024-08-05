package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.Slide;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideDao extends MongoRepository<Slide, String> {}
