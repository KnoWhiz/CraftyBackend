// package org.curastone.Crafty.controller;
//
// import java.util.HashMap;
// import java.util.Map;
// import org.bson.types.ObjectId;
// import org.curastone.Crafty.dao.CourseDao;
// import org.curastone.Crafty.model.Course;
// import org.curastone.Crafty.service.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
//
// @RestController
//// @RequestMapping("/course")
// public class CourseController {
//
//  @Autowired private CourseDao courseDao;
//
//  @PostMapping("/course")
//  public Map<String, String> createCourse(@RequestBody Course course) {
//    // Initialize empty steps for the course rn
//    Map<String, String> steps = new HashMap<>();
//    steps.put("note", null);
//    steps.put("slides", null);
//    steps.put("script", null);
//    steps.put("voice_over", null);
//    steps.put("video", null);
//
//    //    course.setSteps(steps);
//    //    Course savedCourse = courseDao.save(course);
//
//    // Map<String, String> response = new HashMap<>();
//    // response.put("course_id", savedCourse.getId().toString());
//    return steps;
//  }
//
//  @GetMapping("/course/{id}")
//  public Map<String, Object> getCourse(@PathVariable ObjectId id) {
//    Course course = courseDao.findById(id).orElse(null);
//    if (course == null) {
//      return new HashMap<>();
//    }
//
//    Map<String, Object> response = new HashMap<>();
//    response.put("course_type", course.getType());
//    response.put("steps", course.getSteps());
//    return response;
//  }
// }

package org.curastone.Crafty.controller;

import java.util.Map;
import org.bson.types.ObjectId;
import org.curastone.Crafty.model.Course;
import org.curastone.Crafty.service.CourseService;
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
    Course savedCourse = courseService.createCourse(course);
    return ResponseEntity.ok()
        .body(
            "{\"course_id\":\""
                + savedCourse.getId().toString()
                + "\"}"
                + "{\"steps\":\""
                + savedCourse.getSteps()
                + "\"}");
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCourse(@PathVariable("id") ObjectId courseId) {
    Course course = courseService.getCourse(courseId);
    return ResponseEntity.ok().body(buildCourseResponse(course));
  }

  private Map<String, Object> buildCourseResponse(Course course) {
    return Map.of(
        "course_type", course.getType(),
        "steps", course.getSteps());
  }
}
