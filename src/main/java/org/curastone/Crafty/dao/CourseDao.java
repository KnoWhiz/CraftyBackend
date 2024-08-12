package org.curastone.Crafty.dao;

import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseDao extends MongoRepository<Course, ObjectId> {

  
}
