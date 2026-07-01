package com.framework.tests.advanced;

import com.framework.endpoints.HttpBinAPI;
import com.framework.tests.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadDownloadTest extends BaseTest {

    @Test(groups = {"regression", "files"}, description = "Verify multipart form-data file upload")
    public void testFileUpload() throws IOException {
        // Dynamically create a temporary text file inside target directory to prevent workspace clutter
        File tempFile = File.createTempFile("sdet_interview_upload", ".txt");
        tempFile.deleteOnExit();
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("REST Assured Framework reference upload text payload.");
        }

        log.info("Created temporary file for upload testing: {}", tempFile.getAbsolutePath());

        Response response = HttpBinAPI.uploadFile(tempFile);
        
        // Assert successful upload
        response.then().statusCode(200);

        // HttpBin echoes the multipart parameters back in the "files" or "form" attributes of the response body
        String responseString = response.getBody().asString();
        assertThat(responseString).contains("REST Assured Framework reference");
        
        log.info("Multipart file upload verified successfully!");
    }

    @Test(groups = {"regression", "files"}, description = "Verify binary stream download validation")
    public void testFileDownload() {
        int targetByteSize = 64; // Requesting exactly 64 bytes

        Response response = HttpBinAPI.downloadBytes(targetByteSize);
        
        // Validate code 200
        response.then().statusCode(200);

        // Convert the response stream into a raw byte array
        byte[] downloadedBytes = response.asByteArray();
        log.info("Successfully downloaded binary byte stream. Received byte size: {}", downloadedBytes.length);

        // Assert size match
        assertThat(downloadedBytes.length).isEqualTo(targetByteSize);
    }
}
