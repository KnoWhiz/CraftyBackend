package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterDao extends MongoRepository<Chapter, String> {}
