package com.shine.management.shine_management;

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
import com.shine.management.shine_management.model.RegisterUserRequest;
import com.shine.management.shine_management.model.UserResponse;
import com.shine.management.shine_management.model.UserUpdateRequest;
import com.shine.management.shine_management.model.WebResponse;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
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
    void testRegisterSuccess() throws Exception {
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
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
        });
    }

    @Test
    void getUserWithUnauthorizedToken() throws Exception{
        mockMvc.perform(
            get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "notfound")
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
    void getUserSuccess() throws Exception{
        // Set Data
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() + 10000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNull(response.getErrors()); 
                assertEquals(response.getData().getUsername(), user.getUsername());
                assertEquals(response.getData().getName(), user.getName());
            }
        );
    }

    @Test
    void getUserExpiredToken() throws Exception{
        // Set Data
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() - 10000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
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
    void updateUserWithUnauthorizedToken() throws Exception{
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("test");
        request.setPassword("test");

        mockMvc.perform(
            patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "notfound")
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
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() + 10000L);
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("rahasia");
        request.setPassword("rahasia");

        mockMvc.perform(
            patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNull(response.getErrors()); 
                assertEquals(response.getData().getName(), request.getName());
                assertEquals(response.getData().getUsername(), user.getUsername());

                User userDB = userRepository.findById("test").orElse(null);
                assertNotNull(userDB);
                assertTrue(BCrypt.checkpw(request.getPassword(), userDB.getPassword()));
            }
        );
    }

    @Test
    void updateUserFailedBecauseTokenExpired() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() - 10000L);
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("rahasia");
        request.setPassword("rahasia");

        mockMvc.perform(
            patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
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
}
