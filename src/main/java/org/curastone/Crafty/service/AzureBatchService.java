package org.curastone.Crafty.service;

import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.protocol.models.CloudJob;
import com.microsoft.azure.batch.protocol.models.JobAddParameter;
import com.microsoft.azure.batch.protocol.models.PoolInformation;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureBatchService {

  private final String batchUrl;
  private final String batchAccountName;
  private final String batchAccountKey;

  private BatchClient batchClient;

  public AzureBatchService(
      @Value("${azure.batch.url}") String batchUrl,
      @Value("${azure.batch.accountName}") String batchAccountName,
      @Value("${azure.batch.accountKey}") String batchAccountKey) {
    this.batchUrl = batchUrl;
    this.batchAccountName = batchAccountName;
    this.batchAccountKey = batchAccountKey;
    initializeBatchClient();
  }

  private void initializeBatchClient() {
    BatchSharedKeyCredentials credentials =
        new BatchSharedKeyCredentials(batchUrl, batchAccountName, batchAccountKey);
    batchClient = BatchClient.open(credentials);
  }

  //  public String createBatchJob(String jobId, String poolId) {
  //    JobAddParameter jobAddParameter =
  //        new JobAddParameter().withId(jobId).withPoolInfo(new
  // PoolInformation().withPoolId(poolId));
  //
  //    batchClient.jobOperations().createJob(jobAddParameter);
  //    return jobId;
  //  }
  public void createBatchJob(String jobId, String poolId) {
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

  //  public String getJobStatus(String jobId) {
  //    CloudJob job = batchClient.jobOperations().getJob(jobId);
  //    return job.state().toString();
  //  }
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
}
