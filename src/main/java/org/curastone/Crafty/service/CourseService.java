package org.curastone.Crafty.service;

import java.time.Instant;
import java.util.*;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.CourseDao;
import org.curastone.Crafty.exception.ResourceNotFoundException;
import org.curastone.Crafty.model.*;
import org.curastone.Crafty.model.Course.CourseStatus;
import org.curastone.Crafty.model.Course.CourseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

  private final CourseDao courseDao;

  @Autowired
  public CourseService(CourseDao courseDao) {
    this.courseDao = courseDao;
  }

  public Course getCourseById(ObjectId courseId) throws ResourceNotFoundException {
    return courseDao.findById(courseId).orElseThrow(ResourceNotFoundException::new);
  }

  public List<Course> getCoursesByUserIdExcludingDeleted(ObjectId userId) {
    return courseDao
        .findByUserIdAndStatusNot(userId, CourseStatus.DELETED)
        .orElse(new ArrayList<>());
  }

  public int getCreatedCourseCountByUserId(ObjectId userId) {
    return courseDao.countByUserId(userId);
  }

  public int getCreatedCourseCountByUserIdAndCourseType(ObjectId userId, CourseType courseType) {
    return courseDao.countByUserIdAndType(userId, courseType);
  }

  //  public Course saveCourse(Course course) {
  //    return courseDao.save(course);
  //  }
  public Course addCourse(ObjectId userId, Course course) {
    Course newCourse =
        course.toBuilder()
            .id(new ObjectId())
            .userId(userId)
            .creationTime(Instant.now())
            .lastUpdatedTime(Instant.now())
            .build();
    return courseDao.save(newCourse);
  }
}
