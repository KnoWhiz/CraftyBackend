package org.curastone.Crafty.service;

import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.CourseDao;
import org.curastone.Crafty.model.Course;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

  private final CourseDao courseDao;

  public CourseService(CourseDao courseDao) {
    this.courseDao = courseDao;
  }

  public Course createCourse(Course course) {
    Map<String, String> steps = new HashMap<>();
    steps.put("note", null);
    steps.put("slides", null);
    steps.put("script", null);
    steps.put("voice_over", null);
    steps.put("video", null);
    course.setSteps(steps);

    return courseDao.save(course);
  }

  public Course getCourse(ObjectId courseId) {
    return courseDao.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
  }
}
