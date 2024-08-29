package org.curastone.Crafty.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AzureConfig {

  private final String batchUrl;
  private final String batchAccountName;
  private final String batchAccountKey;
  private final String poolId;
  private final String containerImage;
  private final String containerRunOptions;
  private final String openaiApiKey;
  private final String storageConnectionString;
  private final String containerName;

  public AzureConfig(
      @Value("${azure.batch.url}") String batchUrl,
      @Value("${azure.batch.accountName}") String batchAccountName,
      @Value("${azure.batch.accountKey}") String batchAccountKey,
      @Value("${azure.batch.poolId}") String poolId,
      @Value("${azure.batch.containerImage}") String containerImage,
      @Value("${azure.batch.containerRunOptions}") String containerRunOptions,
      @Value("${azure.batch.openaiApiKey}") String openaiApiKey,
      @Value("${azure.storage.connection-string}") String storageConnectionString,
      @Value("${azure.storage.container-name}") String containerName) {
    this.batchUrl = batchUrl;
    this.batchAccountName = batchAccountName;
    this.batchAccountKey = batchAccountKey;
    this.poolId = poolId;
    this.containerImage = containerImage;
    this.containerRunOptions = containerRunOptions;
    this.openaiApiKey = openaiApiKey;
    this.storageConnectionString = storageConnectionString;
    this.containerName = containerName;
  }
}
