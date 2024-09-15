package org.curastone.Crafty.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.curastone.Crafty.dao.CourseDao;
import org.curastone.Crafty.model.Course;
import org.curastone.Crafty.model.Step;
import org.curastone.Crafty.service.AzureBatchService;
import org.curastone.Crafty.service.AzureBlobService;
import org.curastone.Crafty.service.CourseService;
import org.curastone.Crafty.service.StepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/step")
public class StepController {

  private final AzureBatchService azureBatchService;
  private final AzureBlobService azureBlobService;

    @Autowired
  public StepController(AzureBatchService azureBatchService, AzureBlobService azureBlobService, CourseDao courseDao, CourseService courseService) {
    this.azureBatchService = azureBatchService;
    this.azureBlobService = azureBlobService;
    }


  @PostMapping
  public ResponseEntity<String> submitStep(@RequestBody Step step) {
    if (step.getCourseId() == null || step.getStepType() == null || step.getParameters() == null) {
      throw new IllegalArgumentException("Missing required parameters");
    }

    String jobId = "jobId-" + step.getCourseId() + "-" + step.getStepType();
    String taskId = "taskId-" + jobId;
    String commandLine = "";
    try {
      Course course = CourseService.getCourse(step.getCourseId());
      if (course == null || course.getApiKey() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("API key is missing for the course");
      }
      commandLine = StepService.buildCmd(step);
      azureBatchService.createBatchJob(jobId);
      azureBatchService.submitTask(jobId, taskId, commandLine, course.getApiKey());

      step = step.toBuilder().jobId(jobId).build();
      azureBatchService.saveStep(step);
      //      Step savedStep = azureBatchService.saveStep(step);
      //      Map<String, String> response = new HashMap<>();
      //      response.put("cmdL", commandLine);
      //      response.put("step_id", savedStep.getId().toString());
      //      response.put("job_id", savedStep.getJobId());
      //      response.put("task_id", taskId);
      //      return response;

      // Return the task status
      // return "Task Status: " ;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create batch job and task", e);
    }
    return null;
  }

  @GetMapping("/status/{courseId}")
  public ResponseEntity<Map<String, Object>> getStepStatus(
      @PathVariable String courseId, @RequestParam String stepName) {
    System.out.println("Received request for courseId: " + courseId + ", stepName: " + stepName);
    String jobId = "jobId-" + courseId + "-" + stepName;
    Map<String, Object> response = new HashMap<>();
    try {
      Map<String, Object> taskStatus = azureBatchService.getTaskStatus(jobId);
      // response.put("jobId", jobId);
      response.put("taskStatus", taskStatus);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.put("status", "error");
      response.put("message", e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/download/{courseId}")
  public ResponseEntity<String> downloadFile(
      @PathVariable String courseId, HttpServletResponse response) {
    try {
      String folderName = courseId + "/";
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=" + courseId + ".zip");

      azureBlobService.zipFilesToResponse(response, folderName);
      return null;

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to download folder: " + e.getMessage());
    }
  }

  @PutMapping("/{courseId}/upload")
  public ResponseEntity<String> uploadFile(
      @PathVariable String courseId, @RequestParam("file") MultipartFile zipFile) {

    try {
      azureBlobService.unzipAndUploadToBlob(zipFile, courseId);

      return ResponseEntity.ok(
          "Zip file uploaded and contents extracted successfully to Azure Blob Storage under: "
              + courseId);

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload and extract zip file: " + e.getMessage());
    }
  }
}
