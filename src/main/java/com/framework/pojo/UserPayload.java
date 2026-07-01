package com.framework.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Plain Old Java Object (POJO) representing the user profile payload.
 * Used when creating or updating users.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPayload {
    /** Target user full name. */
    private String name;

    /** Target user job position. */
    private String job;

    /** Target user email address. */
    private String email;

    /** Target user password credentials. */
    private String password;
}
