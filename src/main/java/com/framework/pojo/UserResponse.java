package com.framework.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Plain Old Java Object (POJO) representing the serialized response for User profiles.
 * Captures both basic lookup attributes and audit timestamps from REST actions.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    
    /** Unique numeric identifier for the user record. */
    private int id;

    /** Full name of the user. */
    private String name;

    /** Email address associated with the user profile. */
    private String email;

    /** Phone contact number associated with the user profile. */
    private String phone;

    /** Personal or corporate website link associated with the user profile. */
    private String website;

    /** Job description or position name assigned to the user. */
    private String job;

    /** Timestamp indicating user creation metadata. */
    private String createdAt;

    /** Timestamp indicating the user record's latest update audit. */
    private String updatedAt;
}
