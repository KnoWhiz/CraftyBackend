package org.curastone.Crafty.controller;

import io.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.curastone.Crafty.exception.ResourceNotFoundException;
import org.curastone.Crafty.model.Course;
import org.curastone.Crafty.model.Course.CourseType;
import org.curastone.Crafty.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
  private final CourseService courseService;

  @Autowired
  public CourseController(CourseService courseService) {

    this.courseService = courseService;
  }

  @PostMapping("/add/{userId}")
  public ResponseEntity<?> addCourse(@PathVariable ObjectId userId, @RequestBody Course course) {
    try {
      Course newCourse = courseService.addCourse(userId, course);
      return ResponseEntity.ok(newCourse);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Fetches a specific course based on its unique ID.
   *
   * @param courseId the ID of the course to be retrieved
   * @return a ResponseEntity containing the details of the specified course, or a 404 Not Found
   *     status if the course is not found
   */
  @GetMapping("/{courseId}")
  public ResponseEntity<?> getCourseByCourseId(
      @ApiParam(required = true) @PathVariable ObjectId courseId) {
    try {
      Course course = courseService.getCourseById(courseId);
      // authenticateService.checkCourseOwnership(course);
      return ResponseEntity.ok(course);
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Fetches all courses associated with a specific user, excluding courses with a "Deleted" status.
   *
   * @param userId the ID of the user for which to retrieve courses
   * @return a ResponseEntity containing a list of courses for the specified user, or a 404 Not
   *     Found status if no courses are found
   */
  @GetMapping("/byUser/{userId}")
  public ResponseEntity<?> getCoursesByUserId(
      @ApiParam(required = true) @PathVariable ObjectId userId) {
    try {
      // authenticateService.checkUserOwnership(userId);
      return ResponseEntity.ok(courseService.getCoursesByUserIdExcludingDeleted(userId));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Fetches the number of courses created by a specific user, including deleted courses.
   *
   * @param userId the user ID
   * @return the number of courses created by the user
   */
  @GetMapping("/createdCountByUser/{userId}")
  public ResponseEntity<?> getCreatedCourseCountByUserId(
      @ApiParam(required = true) @PathVariable ObjectId userId) {
    try {
      // authenticateService.checkUserOwnership(userId);
      return ResponseEntity.ok(courseService.getCreatedCourseCountByUserId(userId));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Fetches the number of courses created by a specific user and course type, including deleted
   * courses.
   *
   * @param userId the user ID
   * @param courseType the course type
   * @return the number of courses created
   */
  @GetMapping("/createdCountByUser/{userId}/{courseType}")
  public ResponseEntity<?> getCreatedCourseCountByUserIdAndCourseType(
      @ApiParam(required = true) @PathVariable ObjectId userId,
      @ApiParam(required = true) @PathVariable CourseType courseType) {
    try {
      // authenticateService.checkUserOwnership(userId);
      return ResponseEntity.ok(
          courseService.getCreatedCourseCountByUserIdAndCourseType(userId, courseType));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
