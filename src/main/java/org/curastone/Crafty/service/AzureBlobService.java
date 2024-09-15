package org.curastone.Crafty.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.curastone.Crafty.config.AzureConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  public void unzipAndUploadToBlob(MultipartFile zipFile, String courseId) throws IOException {
    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

    try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
      ZipEntry zipEntry;

      while ((zipEntry = zis.getNextEntry()) != null) {
        if (zipEntry.isDirectory()) {
          continue;
        }

        String blobPath = courseId + "/" + zipEntry.getName().replace("\\", "/");

        BlobClient blobClient = containerClient.getBlobClient(blobPath);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[8192];
        int bytesRead;
        while ((bytesRead = zis.read(temp)) != -1) {
          buffer.write(temp, 0, bytesRead);
        }

        byte[] fileBytes = buffer.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
        blobClient.upload(inputStream, fileBytes.length, true); // Overwrite if exists
        System.out.println("Uploaded: " + zipEntry.getName() + " to " + blobPath);
      }
    } catch (Exception e) {
      throw new IOException("Failed to unzip and upload file: " + e.getMessage(), e);
    }
  }

  public void zipFilesToResponse(HttpServletResponse response, String folderName)
      throws IOException {
    List<BlobItem> blobs = listBlobsInFolder(folderName);

    if (blobs.isEmpty()) {
      System.out.println("No blobs found in folder: " + folderName);
      return;
    }

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
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
        String relativePath = blobName.substring(folderName.length());
        ZipEntry zipEntry = new ZipEntry(relativePath);
        zipOutputStream.putNextEntry(zipEntry);
        try (InputStream blobInputStream = blobClient.openInputStream()) {
          IOUtils.copy(blobInputStream, zipOutputStream);
          System.out.println("Zipped: " + blobName + " as " + relativePath);
        } catch (Exception e) {
          System.err.println("Failed to zip blob: " + blobName + " - " + e.getMessage());
        }

        zipOutputStream.closeEntry();
      }

      zipOutputStream.finish();
      System.out.println("Zipping complete for folder: " + folderName);
    } catch (IOException e) {
      throw new IOException("Failed to stream files: " + e.getMessage(), e);
    }
  }
}
