package com.monniserver.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    public String username;

    @NotBlank(message = "Password is required")
    public String password;
}