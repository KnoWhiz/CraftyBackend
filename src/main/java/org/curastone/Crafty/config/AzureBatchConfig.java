package org.curastone.Crafty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class AzureBatchConfig {

    private final String batchUrl;
    private final String batchAccountName;
    private final String batchAccountKey;
    private final String poolId;
    private final String containerImage;
    private final String containerRunOptions;
    private final String openaiApiKey;

    public AzureBatchConfig(
            @Value("${azure.batch.url}") String batchUrl,
            @Value("${azure.batch.accountName}") String batchAccountName,
            @Value("${azure.batch.accountKey}") String batchAccountKey,
            @Value("${azure.batch.poolId}") String poolId,
            @Value("${azure.batch.containerImage}") String containerImage,
            @Value("${azure.batch.containerRunOptions}") String containerRunOptions,
            @Value("${azure.batch.openaiApiKey}") String openaiApiKey) {
        this.batchUrl = batchUrl;
        this.batchAccountName = batchAccountName;
        this.batchAccountKey = batchAccountKey;
        this.poolId = poolId;
        this.containerImage = containerImage;
        this.containerRunOptions = containerRunOptions;
        this.openaiApiKey = openaiApiKey;
    }
}
