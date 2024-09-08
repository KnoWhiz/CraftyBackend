package org.curastone.Crafty.service;

import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.protocol.models.*;
import com.microsoft.azure.batch.protocol.models.CloudTask;
import com.microsoft.azure.batch.protocol.models.TaskExecutionInformation;
import com.microsoft.azure.batch.protocol.models.TaskFailureInformation;
import com.microsoft.azure.batch.protocol.models.TaskState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.curastone.Crafty.config.AzureConfig;
import org.curastone.Crafty.dao.StepDao;
import org.curastone.Crafty.model.Step;
import org.springframework.stereotype.Service;

//

@Service
public class AzureBatchService {

  private final String batchUrl;
  private final String batchAccountName;
  private final String batchAccountKey;
  private final String poolId;
  private final String containerImage;
  private final String containerRunOptions;
  private final String storageConnectionString;
  private final String containerName;
  private final String ffmpegPath;

  private final StepDao stepDao;
  private final String openaiApiKey;
  private BatchClient batchClient;

  //  public AzureBatchTaskStatusChecker(BatchClient batchClient) {
  //    this.batchClient = batchClient;
  //  }

  public AzureBatchService(AzureConfig azureConfig, StepDao stepDao) {
    this.batchUrl = azureConfig.getBatchUrl();
    this.batchAccountName = azureConfig.getBatchAccountName();
    this.batchAccountKey = azureConfig.getBatchAccountKey();
    this.poolId = azureConfig.getPoolId();
    this.containerImage = azureConfig.getContainerImage();
    this.containerRunOptions = azureConfig.getContainerRunOptions();
    this.openaiApiKey = System.getenv("OPENAI_API_KEY");
    this.storageConnectionString = azureConfig.getStorageConnectionString();
    this.containerName = azureConfig.getContainerName();
    this.ffmpegPath = azureConfig.getFfmpegPath();
    this.stepDao = stepDao;
    //
    // Initialize the BatchClient using shared key credentials
    BatchSharedKeyCredentials credentials =
        new BatchSharedKeyCredentials(batchUrl, batchAccountName, batchAccountKey);
    // this.batchClient = BatchClient.open(credentials);
    //
    initializeBatchClient();
  }

  public Map<String, Object> getTaskStatus(String jobId) throws Exception {
    List<CloudTask> tasks = batchClient.taskOperations().listTasks(jobId);
    List<Map<String, Object>> taskStatusList = new ArrayList<>();

    for (CloudTask task : tasks) {
      TaskState taskState = task.state();
      TaskExecutionInformation execInfo = task.executionInfo();
      Map<String, Object> taskStatus = new HashMap<>();
      taskStatus.put("taskId", task.id());
      taskStatus.put("state", taskState.toString());

      if (taskState == TaskState.COMPLETED) {
        if (execInfo.exitCode() != 0) {
          // Task failed
          TaskFailureInformation failureInfo = execInfo.failureInfo();
          taskStatus.put("status", "failed");
          taskStatus.put("error", failureInfo.message());
          sendErrorToBackend(task.id(), failureInfo.message()); // send to java backend
        } else {
          taskStatus.put("status", "completed");
          taskStatus.put("message", "Task completed successfully.");
        }
      }
      taskStatusList.add(taskStatus);
    }
    Map<String, Object> response = new HashMap<>();
    response.put("tasks", taskStatusList);
    return response;
  }

  private void sendErrorToBackend(String taskId, String errorMessage) {
    System.out.println("Sending error for task " + taskId + ": " + errorMessage);
  }

  private void initializeBatchClient() {
    if (batchUrl == null || batchAccountName == null || batchAccountKey == null) {
      throw new IllegalStateException("Azure Batch configuration properties are not set");
    }
    BatchSharedKeyCredentials credentials =
        new BatchSharedKeyCredentials(batchUrl, batchAccountName, batchAccountKey);
    batchClient = BatchClient.open(credentials);
  }

  public void createBatchJob(String jobId) {
    try {
      JobAddParameter jobAddParameter =
          new JobAddParameter()
              .withId(jobId)
              .withPoolInfo(new PoolInformation().withPoolId(poolId));

      batchClient.jobOperations().createJob(jobAddParameter);

    } catch (IOException e) {
      throw new RuntimeException("Failed to create batch job due to an IO error", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create batch job", e);
    }
  }

  public String getJobStatus(String jobId) {
    try {
      CloudJob job = batchClient.jobOperations().getJob(jobId);
      return job.state().toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to get job status due to an IO error", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get job status", e);
    }
  }

  public void submitTask(String jobId, String taskId, String commandLine) {
    try {
      List<EnvironmentSetting> environmentList =
          Map.of(
                  "OPENAI_API_KEY",
                          openaiApiKey,
                  "AZURE_STORAGE_CONNECTION_STRING",
                  storageConnectionString,
                  "AZURE_CONTAINER_NAME",
                  containerName,
                  "IMAGEIO_FFMPEG_EXE",
                  ffmpegPath)
              .entrySet()
              .stream()
              .map(
                  entry ->
                      new EnvironmentSetting().withName(entry.getKey()).withValue(entry.getValue()))
              .collect(Collectors.toList());

      TaskContainerSettings containerSettings =
          new TaskContainerSettings()
              .withImageName(containerImage)
              .withContainerRunOptions(containerRunOptions);

      TaskAddParameter taskAddParameter =
          new TaskAddParameter()
              .withId(taskId)
              .withCommandLine(commandLine)
              .withContainerSettings(containerSettings)
              .withEnvironmentSettings(environmentList);

      batchClient.taskOperations().createTask(jobId, taskAddParameter);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create batch task due to an IO error", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create batch task", e);
    }
  }

  public Step saveStep(Step step) {
    return stepDao.save(step);
  }

  public Step findStepById(String id) {
    return stepDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Step not found"));
  }
}
