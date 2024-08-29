package org.curastone.Crafty.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import org.bson.types.ObjectId;
import org.curastone.Crafty.config.AzureConfig;
import org.springframework.stereotype.Service;

@Service
public class AzureBlobService {

  private final BlobServiceClient blobServiceClient;
  private final String containerName;

  public AzureBlobService(AzureConfig azureConfig) {
    this.blobServiceClient =
        new BlobServiceClientBuilder()
            .connectionString(azureConfig.getStorageConnectionString())
            .buildClient();
    this.containerName = azureConfig.getContainerName();
  }

  public URL generatePresignedUrl(ObjectId courseId, String stepType, String filename) {
    OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(7);
    ;
    String blobName =
        String.format("outputFile/%s/%s/%s", courseId.toHexString(), stepType, filename);

    // Set the SAS permissions including read, write, delete, and list
    BlobSasPermission blobSasPermission =
        new BlobSasPermission()
            .setReadPermission(true)
            .setWritePermission(true)
            .setDeletePermission(true)
            .setListPermission(true);

    // Set the SAS values, including protocol, start time, and expiry time
    BlobServiceSasSignatureValues values =
        new BlobServiceSasSignatureValues(expiryTime, blobSasPermission)
            .setStartTime(
                OffsetDateTime.now()
                    .minusMinutes(500)) // Optional: adjust start time to account for clock skew
            .setProtocol(SasProtocol.HTTPS_ONLY); // Ensure only HTTPS is allowed

    System.out.println("Generated Blob Name: " + blobName);
    System.out.println("SAS Signature Values: " + values);

    // Generate the SAS token
    String sasToken =
        blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(blobName)
            .generateSas(values);

    System.out.println("Generated SAS Token: " + sasToken);

    // Construct the full URL with the SAS token
    String fullUrl =
        String.format(
            "https://%s.blob.core.windows.net/%s/%s?%s",
            blobServiceClient.getAccountName(), containerName, blobName, sasToken);

    System.out.println("Full URL: " + fullUrl);

    try {
      return new URL(fullUrl);
    } catch (MalformedURLException e) {
      System.err.println("Failed to generate pre-signed URL: " + fullUrl);
      e.printStackTrace();
      throw new RuntimeException("Failed to generate pre-signed URL: " + fullUrl, e);
    }
  }
}
