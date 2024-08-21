package org.curastone.Crafty.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.curastone.Crafty.dao.StepDao;
import org.curastone.Crafty.model.Step;
import org.curastone.Crafty.service.AzureBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/step")
public class StepController {

  private final StepDao stepDao;
  private final AzureBatchService azureBatchService;

  @Autowired
  public StepController(StepDao stepDao, AzureBatchService azureBatchService) {
    this.stepDao = stepDao;
    this.azureBatchService = azureBatchService;
  }

  @PostMapping
  public Map<String, String> submitStep(@RequestBody Step step) throws IOException {
    if (step.getCourseId() == null || step.getStepType() == null || step.getParameters() == null) {
      throw new IllegalArgumentException("Missing required parameters");
    }
    String jobId =
        "jobId-" + step.getCourseId() + "-" + step.getStepType() + "-" + System.currentTimeMillis();
    String taskId = "taskId-" + jobId;
    try {
      azureBatchService.createBatchJob(jobId);
      String commandLine =
          "step " + step.getStepType() + " --topic '" + step.getParameters().get("topic") + "'";
      azureBatchService.submitTask(jobId, taskId, commandLine);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create batch job and task", e);
    }

    Step savedStep = stepDao.save(step.toBuilder().jobId(jobId).build());
    Map<String, String> response = new HashMap<>();
    response.put("step_id", savedStep.getId().toString());
    response.put("job_id", savedStep.getJobId());
    response.put("task_id", taskId);
    return response;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getStepStatus(@PathVariable String id) { // input is stepId, not jobId
    Step step = stepDao.findById(id).orElse(null);
    if (step == null) {
      throw new IllegalArgumentException("Step not found");
    }
    // Fetch job status from Azure Batch
    String batchJobStatus = azureBatchService.getJobStatus(step.getJobId());
    // String batchJobStatus = "TRUE";
    step.setStepStatus(batchJobStatus);
    // TODO: get result from batch (do we still need that?)
    step.setResult("");
    stepDao.save(step);

    Map<String, Object> response = new HashMap<>();
    response.put("step_status", step.getStepStatus());

    return response;
  }
}
