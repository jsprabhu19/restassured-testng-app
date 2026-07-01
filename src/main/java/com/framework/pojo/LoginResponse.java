package com.framework.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Plain Old Java Object (POJO) representing the response returned by ReqRes authentication requests.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
    /** Extracted login token returned upon successful authentication. */
    private String token;

    /** Error description string returned upon failed authentication. */
    private String error;
}
