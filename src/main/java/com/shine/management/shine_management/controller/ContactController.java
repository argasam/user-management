package com.shine.management.shine_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.ContactRequest;
import com.shine.management.shine_management.model.ContactResponse;
import com.shine.management.shine_management.model.WebResponse;
import com.shine.management.shine_management.service.ContactService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class ContactController {
    
    @Autowired
    private ContactService contactService;

    @PostMapping(
        path = "/api/contact",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> saveContact(@RequestBody ContactRequest request, User user) {
        //TODO: process POST request
        ContactResponse response = contactService.addContact(request, user);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping(
        path = "/api/contact/{contactId}",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> getContactId(User user, @PathVariable("contactId") String id) {
        ContactResponse response = contactService.getContactbyId(user, id);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PutMapping(
        path = "/api/contact/{contactId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> putContact(
        @PathVariable("contactId") String id, 
        @RequestBody ContactRequest request, 
        User user) {
        //TODO: process PUT request
        ContactResponse response = contactService.updateContact(request, user, id);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @DeleteMapping(
        path = "api/contact/{contactId}",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteContact(
        User user,
        @PathVariable("contactId") String id){
        contactService.deleteContact(user, id);
        return WebResponse.<String>builder().data("Ok").build();

    }
}
