package com.example.cartasIntercambio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucket", "mi-bucket");
        ReflectionTestUtils.setField(s3Service, "region", "us-east-2");
    }

    @Test
    void testUploadFile() throws IOException {
        // Arrange
        String originalFileName = "foto.png";
        String contentType = "image/png";
        byte[] content = "contenido test".getBytes();

        MultipartFile multipartFile = new MockMultipartFile(
                "file", originalFileName, contentType, content
        );

        // Act
        String url = s3Service.uploadFile(multipartFile);

        // Assert
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertTrue(url.contains("https://mi-bucket.s3.us-east-2.amazonaws.com/"));
        assertTrue(url.endsWith(originalFileName));
    }

}