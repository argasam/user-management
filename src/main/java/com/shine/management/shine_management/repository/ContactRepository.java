package com.shine.management.shine_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shine.management.shine_management.entity.Contact;
import com.shine.management.shine_management.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>{
    
    Optional<Contact> findFirstByUserAndId(User user, String contactId);
}
