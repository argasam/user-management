package com.shine.management.shine_management.IntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.LoginRequest;
import com.shine.management.shine_management.model.RegisterUserRequest;
import com.shine.management.shine_management.repository.ContactRepository;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class GetContact {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setPassword("test");
        registerUserRequest.setName("Test");

        mockMvc.perform(
            post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
            status().isOk());

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("test");
        
        mockMvc.perform(
        post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk());
    }
}
