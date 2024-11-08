package com.shine.management.shine_management.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shine.management.shine_management.entity.Contact;
import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.ContactRequest;
import com.shine.management.shine_management.model.ContactResponse;
import com.shine.management.shine_management.repository.ContactRepository;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ValidationService validator;
    
    private ContactResponse generateResponse(Contact contact){
        return ContactResponse.builder()
        .id(contact.getId())
        .firstName(contact.getFirstName())
        .lastName(contact.getLastName())
        .email(contact.getEmail())
        .phone(contact.getPhone())
        .build();
    }

    @Transactional
    public ContactResponse addContact(ContactRequest request, User user){
        validator.validation(request);
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return generateResponse(contact);
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactbyId(User user, String id){
        Contact response = contactRepository.findFirstByUserAndId(user, id)
        .orElseThrow(
           () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found in this user")
        );

        return generateResponse(response);

    }

    @Transactional
    public ContactResponse updateContact(ContactRequest request, User user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
        .orElseThrow(
           () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found in this user")
        );

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return generateResponse(contact);
    }

    @Transactional
    public void deleteContact(User user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
        .orElseThrow(
           () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found in this user")
        );

        contactRepository.delete(contact);
    }

}
