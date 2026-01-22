package com.ideaspark.project.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    
    private String url;
    
    private String path;
    
    @JsonProperty("file_name")
    private String fileName;
    
    @JsonProperty("file_extension")
    private String fileExtension;
}
