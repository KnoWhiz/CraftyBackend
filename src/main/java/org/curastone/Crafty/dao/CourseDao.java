package org.curastone.Crafty.dao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Course;
import org.curastone.Crafty.model.Course.CourseStatus;
import org.curastone.Crafty.model.Course.CourseType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseDao extends MongoRepository<Course, ObjectId> {

  Optional<List<Course>> findByUserIdAndStatusNot(ObjectId userId, CourseStatus status);

  int countByUserId(ObjectId userId);

  int countByUserIdAndType(ObjectId userId, CourseType courseType);
}
