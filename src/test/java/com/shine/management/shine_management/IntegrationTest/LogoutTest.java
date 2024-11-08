package com.shine.management.shine_management.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.LoginRequest;
import com.shine.management.shine_management.model.RegisterUserRequest;
import com.shine.management.shine_management.model.WebResponse;
import com.shine.management.shine_management.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class LogoutTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void logoutSuccess() throws Exception{
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setPassword("rahasia");
        registerUserRequest.setName("Test");

        mockMvc.perform(
            post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
            status().isOk());


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerUserRequest.getUsername());
        loginRequest.setPassword(registerUserRequest.getPassword());
        mockMvc.perform(
            post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest))
        ).andExpectAll(
            status().isOk()
        );
            
        User user = userRepository.findById(registerUserRequest.getUsername()).orElse(null);
        log.info(user.getToken());
        assertNotNull(user.getToken());
        mockMvc.perform(
            delete("/api/auth/logout")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                assertNull(response.getErrors());
                assertEquals("OK", response.getData());

                User userDB = userRepository.findById(registerUserRequest.getUsername()).orElse(null);
                assertNotNull(userDB);
                assertNull(userDB.getToken());
                assertNull(userDB.getExpiredAt());
            }
        );
    }

}
