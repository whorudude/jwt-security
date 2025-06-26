package org.example.jwtsecurity.model.authentication;

public record RegisterRequest(String email, String username, String password) {
}

