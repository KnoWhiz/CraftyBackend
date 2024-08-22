package org.curastone.Crafty.service;

import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.protocol.models.CloudJob;
import com.microsoft.azure.batch.protocol.models.EnvironmentSetting;
import com.microsoft.azure.batch.protocol.models.JobAddParameter;
import com.microsoft.azure.batch.protocol.models.PoolInformation;
import com.microsoft.azure.batch.protocol.models.TaskAddParameter;
import com.microsoft.azure.batch.protocol.models.TaskContainerSettings;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.curastone.Crafty.config.AzureBatchConfig;
import org.curastone.Crafty.dao.StepDao;
import org.curastone.Crafty.model.Step;
import org.springframework.stereotype.Service;

@Service
public class AzureBatchService {

  private final String batchUrl;
  private final String batchAccountName;
  private final String batchAccountKey;
  private final String poolId;
  private final String containerImage;
  private final String containerRunOptions;
  private final String openaiApiKey;

  private final StepDao stepDao;
  private BatchClient batchClient;

  public AzureBatchService(AzureBatchConfig azureBatchConfig, StepDao stepDao) {
    this.batchUrl = azureBatchConfig.getBatchUrl();
    this.batchAccountName = azureBatchConfig.getBatchAccountName();
    this.batchAccountKey = azureBatchConfig.getBatchAccountKey();
    this.poolId = azureBatchConfig.getPoolId();
    this.containerImage = azureBatchConfig.getContainerImage();
    this.containerRunOptions = azureBatchConfig.getContainerRunOptions();
    this.openaiApiKey = azureBatchConfig.getOpenaiApiKey();
    this.stepDao = stepDao;
    initializeBatchClient();
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
          Map.of("OPENAI_API_KEY", openaiApiKey).entrySet().stream()
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
