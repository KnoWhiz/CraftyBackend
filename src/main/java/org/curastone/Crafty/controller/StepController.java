package org.curastone.Crafty.controller;

import java.util.HashMap;
import java.util.Map;
import org.curastone.Crafty.model.Step;
import org.curastone.Crafty.service.AzureBatchService;
import org.curastone.Crafty.service.AzureBlobService;
import org.curastone.Crafty.service.StepService;
import org.springframework.beans.factory.annotation.Autowired;
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
    String jobId =
            "jobId-" + step.getCourseId() + "-" + step.getStepType() + "-" + System.currentTimeMillis();
    String taskId = "taskId-" + jobId;
    String commandLine = "";
    try {
      commandLine = StepService.buildCmd(step);
      azureBatchService.createBatchJob(jobId);
      azureBatchService.submitTask(jobId, taskId, commandLine);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create batch job and task", e);
    }

    step = step.toBuilder().jobId(jobId).build();
    azureBatchService.saveStep(step);
//    Map<String, String> response = new HashMap<>();
//    response.put("cmdL", commandLine);
//    response.put("step_id", savedStep.getId().toString());
//    response.put("job_id", savedStep.getJobId());
//    response.put("task_id", taskId);
//    return response;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getStepStatus(@PathVariable String id) { // input is stepId, not jobId
    Step step = azureBatchService.findStepById(id);
    // Fetch job status from Azure Batch
    String batchJobStatus = azureBatchService.getJobStatus(step.getJobId());
    // String batchJobStatus = "TRUE";
    step.setStepStatus(batchJobStatus);
    // TODO: get result from batch (do we still need that?)
    step.setResult("");
    azureBatchService.saveStep(step);
    Map<String, Object> response = new HashMap<>();
    response.put("step_status", step.getStepStatus());

    return response;
  }
}