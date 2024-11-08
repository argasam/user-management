package com.shine.management.shine_management;

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
import com.shine.management.shine_management.model.TokenResponse;
import com.shine.management.shine_management.model.WebResponse;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthControllerTest {
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
    void loginSuccess() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("test");
        
        mockMvc.perform(
        post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNull(response.getErrors()); 
                assertNotNull(response.getData());
            }
        );
    }

    @Test
    void loginNoData() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
            post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNotNull(response.getErrors());
            });
    }

    @Test
    void loginWrongPassword() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        
        mockMvc.perform(
        post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNotNull(response.getErrors()); 
            }
        );
    }

    @Test
    void loginFailed() throws Exception{
        mockMvc.perform(
        delete("/api/auth/logout")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                assertNotNull(response.getErrors());
            }
        );
    }

    @Test
    void logoutSuccess() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() + 10000L);
        userRepository.save(user);

            
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

                User userDB = userRepository.findById(user.getUsername()).orElse(null);
                assertNotNull(userDB);
                assertNull(userDB.getToken());
                assertNull(userDB.getExpiredAt());
            }
        );
    }
}
