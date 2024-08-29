package org.curastone.Crafty.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.curastone.Crafty.model.Step;
import org.curastone.Crafty.service.AzureBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/step")
public class StepController {

    private final AzureBatchService azureBatchService;

    @Autowired
    public StepController(AzureBatchService azureBatchService) {
        this.azureBatchService = azureBatchService;
    }

    @PostMapping
    public Map<String, String> submitStep(@RequestBody Step step) {
        if (step.getStepType() == null || step.getParameters() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }
        String jobId =
                "jobId-" + step.getCourseId() + "-" + step.getStepType() + "-" + System.currentTimeMillis();
        String taskId = "taskId-" + jobId;
        try {
            String commandLine = "";
            azureBatchService.createBatchJob(jobId);
            if (Objects.equals(step.getStepType(), "chapter")){
                commandLine =
                        "step " + step.getStepType() + " --topic '" + step.getParameters().get("topic") + "'";

            } else if (Objects.equals(step.getStepType(), "section")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --sections_per_chapter '" + step.getParameters().get("sections_per_chapter") + "'";
            } else if (Objects.equals(step.getStepType(), "note")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --max_note_expansion_words '" + step.getParameters().get("max_note_expansion_words") + " --chapter '" + step.getParameters().get("chapter") + "'";
            } else if (Objects.equals(step.getStepType(), "slide")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --slides_template_file '" + step.getParameters().get("slides_template_file") + " --content_slide_pages '" + step.getParameters().get("content_slide_pages") + "'";
            } else if (Objects.equals(step.getStepType(), "script")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --chapter '" + step.getParameters().get("chapter") + "'";
            } else if (Objects.equals(step.getStepType(), "voice")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --chapter '" + step.getParameters().get("chapter") + "'";
            } else if (Objects.equals(step.getStepType(), "video")) {
                commandLine =
                        "step " + step.getStepType() + " --course_id 4add517d26" + " --chapter '" + step.getParameters().get("chapter") + "'";
            }



            azureBatchService.submitTask(jobId, taskId, commandLine);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create batch job and task", e);
        }

        step = step.toBuilder().jobId(jobId).build();
        Step savedStep = azureBatchService.saveStep(step);
        Map<String, String> response = new HashMap<>();
        response.put("step_id", savedStep.getId().toString());
        response.put("job_id", savedStep.getJobId());
        response.put("task_id", taskId);
        return response;
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
