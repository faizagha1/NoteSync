package com.faiz.NoteTaking.User;

import org.springframework.lang.NonNull;

import jakarta.validation.constraints.Email;

public record UserRequest(
        @NonNull String firstName,
        @NonNull String lastName,
        @NonNull String username,
        @NonNull @Email String email,
        @NonNull String password) {
}