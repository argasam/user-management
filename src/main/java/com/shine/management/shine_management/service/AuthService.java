package com.shine.management.shine_management.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.LoginRequest;
import com.shine.management.shine_management.model.TokenResponse;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginRequest loginRequest){
        validationService.validation(loginRequest);

        User user = userRepository.findById(loginRequest.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username of password wrong"));

        if (BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setExpiredAt(add30Days());
            userRepository.save(user);

            return TokenResponse.builder()
            .token(user.getToken())
            .expiredAt(user.getExpiredAt())
            .build();

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username of password wrong");
        }
    }

    private Long add30Days() {
        return System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L);
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setExpiredAt(null);
        userRepository.save(user);
    }
}
