package org.curastone.Crafty.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.curastone.Crafty.model.Step;
import org.curastone.Crafty.service.AzureBatchService;
import org.curastone.Crafty.service.AzureBlobService;
import org.curastone.Crafty.service.StepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/step")
public class StepController {

  private final AzureBatchService azureBatchService;
  private final AzureBlobService azureBlobService;

  @Autowired
  public StepController(AzureBatchService azureBatchService, AzureBlobService azureBlobService) {
    this.azureBatchService = azureBatchService;
    this.azureBlobService = azureBlobService;
  }

  @PostMapping
  public void submitStep(@RequestBody Step step) {
    if (step.getCourseId() == null || step.getStepType() == null || step.getParameters() == null) {
      throw new IllegalArgumentException("Missing required parameters");
    }
    String jobId = "jobId-" + step.getCourseId() + "-" + step.getStepType();
    String taskId = "taskId-" + jobId;
    String commandLine = "";
    try {
      commandLine = StepService.buildCmd(step);
      azureBatchService.createBatchJob(jobId);
      azureBatchService.submitTask(jobId, taskId, commandLine);

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
      @PathVariable String courseId, @RequestParam String downloadPath) {
    try {
      File outputDir = new File(downloadPath, "outputs");
      if (!outputDir.exists()) {
        if (!outputDir.mkdirs()) {
          throw new IOException(
              "Failed to create outputs directory: " + outputDir.getAbsolutePath());
        }
      }
      azureBlobService.downloadFolder(courseId + "/", outputDir.getAbsolutePath());

      return ResponseEntity.ok("Folder downloaded successfully using pre-signed URLs");

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to download folder: " + e.getMessage());
    }
  }
}
