package com.framework.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    
    // Standard JSONPlaceholder User fields
    private int id;
    private String name;
    private String email;
    private String phone;
    private String website;

    // Attributes returned during POST/PUT operations
    private String job;
    private String createdAt;
    private String updatedAt;
}
