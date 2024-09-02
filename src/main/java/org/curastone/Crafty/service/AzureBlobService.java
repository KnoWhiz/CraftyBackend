package org.curastone.Crafty.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
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

  public List<BlobItem> listBlobsInFolder(String folderName) {
    List<BlobItem> blobs = new ArrayList<>();
    Deque<String> prefixesToCheck = new ArrayDeque<>();
    prefixesToCheck.add(folderName);

    while (!prefixesToCheck.isEmpty()) {
      String currentPrefix = prefixesToCheck.poll(); // Retrieves and removes the head of the queue

      for (BlobItem blobItem :
          blobServiceClient
              .getBlobContainerClient(containerName)
              .listBlobsByHierarchy(currentPrefix)) {

        if (blobItem.isPrefix()) {
          prefixesToCheck.add(blobItem.getName());
        } else {
          blobs.add(blobItem);
        }
      }
    }

    return blobs;
  }

  public void downloadFolder(String folderName, String localDirPath) throws IOException {
    List<BlobItem> blobs = listBlobsInFolder(folderName);

    if (blobs.isEmpty()) {
      System.out.println("No blobs found in folder: " + folderName);
      return;
    }

    File localDir = new File(localDirPath);
    if (!localDir.exists()) {
      if (!localDir.mkdirs()) {
        throw new IOException("Failed to create local directory: " + localDir.getAbsolutePath());
      }
    }

    for (BlobItem blobItem : blobs) {
      String blobName = blobItem.getName();
      if (!blobName.startsWith(folderName)) {
        System.out.println("Skipping blob not in exact folder: " + blobName);
        continue;
      }
      if (blobName.endsWith("/")) {
        System.out.println("Skipping directory placeholder: " + blobName);
        continue;
      }

      BlobClient blobClient =
          blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName);

      // Construct the local file path, preserving the directory structure
      String relativePath = blobName.substring(folderName.length());
      String localFilePath = localDirPath + File.separator + relativePath;
      File localFile = new File(localFilePath);
      
      File parentDir = localFile.getParentFile();
      if (!parentDir.exists()) {
        if (!parentDir.mkdirs()) {
          throw new IOException("Failed to create directories: " + parentDir.getAbsolutePath());
        } else {
          System.out.println("Created directory: " + parentDir.getAbsolutePath());
        }
      }

      try {
        blobClient.downloadToFile(localFilePath);
        System.out.println("Downloaded: " + blobName + " to " + localFilePath);
      } catch (Exception e) {
        System.err.println("Failed to download blob: " + blobName + " - " + e.getMessage());
      }
    }

    System.out.println("Download complete for folder: " + folderName);
  }
}
