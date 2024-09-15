package org.curastone.Crafty.controller;

import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Course;
import org.curastone.Crafty.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {

  private final CourseService courseService;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @PostMapping
  public ResponseEntity<?> createCourse(@RequestBody Course course) {
    if (course.getApiKey() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body("API key is required for the course");
    }
    Course savedCourse = courseService.createCourse(course);

    Map<String, Object> response = new HashMap<>();
    response.put("course_id", savedCourse.getId().toString());
    response.put("steps", savedCourse.getSteps());
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCourse(@PathVariable("id") ObjectId courseId) {
    Course course = CourseService.getCourse(courseId);
    return ResponseEntity.ok().body(buildCourseResponse(course));
  }

  private Map<String, Object> buildCourseResponse(Course course) {
    return Map.of(
        "course_type", course.getType(),
        "steps", course.getSteps());
  }
}
