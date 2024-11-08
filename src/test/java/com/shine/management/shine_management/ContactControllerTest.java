package com.shine.management.shine_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shine.management.shine_management.entity.Contact;
import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.ContactRequest;
import com.shine.management.shine_management.model.ContactResponse;
import com.shine.management.shine_management.model.WebResponse;
import com.shine.management.shine_management.repository.ContactRepository;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setExpiredAt(System.currentTimeMillis() + 10000L);
        userRepository.save(user);
    }

    @AfterEach
    void cleanUp(){
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addContactUnauthorized()throws Exception{
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("arga");
        contactRequest.setLastName("samosir");
        contactRequest.setEmail("test@gmail.com");
        contactRequest.setPhone("+6282387507445");

        mockMvc.perform(
            post("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "x")
            .content(objectMapper.writeValueAsString(contactRequest))
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
    void addContactSuccess()throws Exception{
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("arga");
        contactRequest.setLastName("samosir");
        contactRequest.setEmail("test@gmail.com");
        contactRequest.setPhone("+6282387507445");

        mockMvc.perform(
            post("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(contactRequest))
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                
                });
                assertNotNull(response.getData());
                assertNull(response.getErrors());
                assertEquals(response.getData().getFirstName(), contactRequest.getFirstName());
                assertEquals(response.getData().getLastName(), contactRequest.getLastName());
                assertEquals(response.getData().getPhone(), contactRequest.getPhone());
                assertEquals(response.getData().getEmail(), contactRequest.getEmail());
            }
        );
    }

    @Test
    void addContactNameOnly()throws Exception{
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("arga");
        contactRequest.setLastName("samosir");
        
        mockMvc.perform(
            post("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(contactRequest))
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                
                });
                assertNotNull(response.getData());
                assertNull(response.getErrors());
                assertEquals(response.getData().getFirstName(), contactRequest.getFirstName());
                assertEquals(response.getData().getLastName(), contactRequest.getLastName());
                assertEquals(response.getData().getPhone(), contactRequest.getPhone());
                assertEquals(response.getData().getEmail(), contactRequest.getEmail());
            }
        );
    }

    @Test
    void addContactInssuficientPhoneNumber()throws Exception{
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("arga");
        contactRequest.setLastName("samosir");
        contactRequest.setEmail("test@gmail.com");
        contactRequest.setPhone("+628238");

        mockMvc.perform(
            post("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(contactRequest))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                
                });
                assertNotNull(response.getErrors());
            }
        );
    }

    @Test
    void addContactOverseasPhoneNumber()throws Exception{
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("arga");
        contactRequest.setLastName("samosir");
        contactRequest.setEmail("test@gmail.com");
        contactRequest.setPhone("+6582387507445");

        mockMvc.perform(
            post("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(contactRequest))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                
                });
                assertNotNull(response.getErrors());
            }
        );
    }

    @Test
    void getContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
            get("/api/contact/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNull(response.getErrors());
                assertNotNull(response.getData());
                assertEquals(response.getData().getFirstName(), contact.getFirstName());
                assertEquals(response.getData().getLastName(), contact.getLastName());
                assertEquals(response.getData().getEmail(), contact.getEmail());
                assertEquals(response.getData().getPhone(), contact.getPhone());
            }
        );
    }

    @Test
    void getContactFailed() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
            get("/api/contact/" + "test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
            status().isNotFound()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNotNull(response.getErrors());
                assertNull(response.getData());
            }
        );
    }

    @Test
    void updateContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);

        ContactRequest request = new ContactRequest();
        request.setFirstName("udin");
        request.setLastName("");
        request.setEmail("test@gmail.com");
        request.setPhone("+6282387507445");
        
        mockMvc.perform(
            put("/api/contact/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNull(response.getErrors());
                assertNotNull(response.getData());
                assertEquals(response.getData().getFirstName(), request.getFirstName());
                assertEquals(response.getData().getLastName(), request.getLastName());
                assertEquals(response.getData().getEmail(), request.getEmail());
                assertEquals(response.getData().getPhone(), request.getPhone());
            }
        );
    }

    @Test
    void updateContactWrongToken() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);

        ContactRequest request = new ContactRequest();
        request.setFirstName("udin");
        request.setLastName("");
        request.setEmail("test@gmail.com");
        request.setPhone("+6282387507445");
        
        mockMvc.perform(
            put("/api/contact/" + "test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isNotFound()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNotNull(response.getErrors());
                assertNull(response.getData());
                }
        );
    }

    @Test
    void deleteContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);
        
        mockMvc.perform(
            delete("/api/contact/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNull(response.getErrors());
                assertNotNull(response.getData());
            }
        );
    }

    @Test
    void deleteContactNoContact() throws Exception{
        User user = userRepository.findById("test").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Arga");
        contact.setLastName("Samosir");
        contact.setEmail("test@gmail.com");
        contact.setPhone("+6282387507445");
        contact.setUser(user);
        contactRepository.save(contact);
        
        mockMvc.perform(
            delete("/api/contact/" + "test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
            status().isNotFound()
        ).andDo(
            result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){

                });
                assertNotNull(response.getErrors());
                assertNull(response.getData());
            }
        );
    }

}
