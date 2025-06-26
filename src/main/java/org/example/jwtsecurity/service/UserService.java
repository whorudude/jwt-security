package org.example.jwtsecurity.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.jwtsecurity.entity.UserEntity;
import org.example.jwtsecurity.exception.authentication.AlreadyRegisteredException;
import org.example.jwtsecurity.model.authentication.AuthenticationRequest;
import org.example.jwtsecurity.model.authentication.AuthenticationResponse;
import org.example.jwtsecurity.model.authentication.RegisterRequest;
import org.example.jwtsecurity.repository.UserRepository;
import org.example.jwtsecurity.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.email()).isPresent())
            throw new AlreadyRegisteredException("Email is already in use");

        if (userRepository.findByUsername(registerRequest.username()).isPresent())
            throw new AlreadyRegisteredException("User with same username is already registered");

        var user = UserEntity.builder()
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()
                )
        );

        var user = userRepository.findByUsername(authenticationRequest.username());

        return new AuthenticationResponse(jwtService.generateToken(user.orElseThrow()));
    }
}

