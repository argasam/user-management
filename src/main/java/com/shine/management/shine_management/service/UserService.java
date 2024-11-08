package com.shine.management.shine_management.service;

    import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shine.management.shine_management.entity.User;
import com.shine.management.shine_management.model.RegisterUserRequest;
import com.shine.management.shine_management.model.UserResponse;
import com.shine.management.shine_management.model.UserUpdateRequest;
import com.shine.management.shine_management.repository.UserRepository;
import com.shine.management.shine_management.security.BCrypt;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){
        
        validationService.validation(request);

        if(userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exist!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder()
        .username(user.getUsername())
        .name(user.getName())
        .build();
    }
    
    @Transactional
    public UserResponse update(User user, UserUpdateRequest request){

        validationService.validation(request);
        
        if (Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        if  (Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
        .username(user.getUsername())
        .name(user.getName())
        .build();
    }

}
