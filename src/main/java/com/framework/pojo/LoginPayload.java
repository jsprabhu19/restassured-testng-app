package com.framework.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Plain Old Java Object (POJO) representing the credentials payload for authentication requests.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class LoginPayload {
    /** Target account email address. */
    private String email;

    /** Target account password. */
    private String password;
}
