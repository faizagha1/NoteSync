package com.faiz.NoteTaking.Authetication;

public record LoginResponse(
                String token,
                String email,
                String message) {

}
