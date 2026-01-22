package com.ideaspark.project.controller;

import com.ideaspark.project.config.JwtAuthenticationInterceptor;
import com.ideaspark.project.model.dto.response.FileUploadResponse;
import com.ideaspark.project.service.OssService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileController.class, properties = "oss.enabled=true")
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OssService ossService;

    @MockBean
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Test
    public void uploadShouldReturnCorrectJson() throws Exception {
        // Mock JwtAuthenticationInterceptor to allow request
        given(jwtAuthenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // Mock OssService
        FileUploadResponse response = FileUploadResponse.builder()
                .url("https://ideaspark.oss-cn-beijing.aliyuncs.com/test-key")
                .path("test-key")
                .fileName("test.txt")
                .fileExtension("txt")
                .build();
        
        given(ossService.uploadFile(any())).willReturn(response);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello".getBytes()
        );

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("上传成功"))
                .andExpect(jsonPath("$.data.url").value("https://ideaspark.oss-cn-beijing.aliyuncs.com/test-key"))
                .andExpect(jsonPath("$.data.path").value("test-key"))
                .andExpect(jsonPath("$.data.file_name").value("test.txt"))
                .andExpect(jsonPath("$.data.file_extension").value("txt"));
    }
}
