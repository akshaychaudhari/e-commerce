package com.example.demo.controllers;

import com.example.demo.ECommerceApplication;
import com.example.demo.exceptionHandlers.UserAlreadyExistsException;
import com.example.demo.model.persistence.entities.Cart;
import com.example.demo.model.persistence.entities.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*
     * Method to get the given user details by userId.
     * */
    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        logger.debug("Getting user by Id " + id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    /*
     * Method to get the given user details by username.
     * */
    @GetMapping("/")
    public ResponseEntity<List<User>> findAllUsers() {
        logger.debug("Getting all users ");
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(users);
    }

    /*
     * Method to get all given user details.
     * */
    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        logger.debug("Getting user by name " + username);
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    /*
     * Method to create new user to the system.
     * */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

        User checkUser = userRepository.findByUsername(createUserRequest.getUsername());
        if (checkUser != null) {
            logger.debug("User Creation failed for user " + createUserRequest.getUsername());
            throw new UserAlreadyExistsException();
        }
        logger.debug("Creating new user" + createUserRequest.getUsername());
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        logger.debug("Checking the password constraints to fulfill");
        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.debug("Error - Either length is less than 7 or pass and confirm password do not match. Unable to create user:" + createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }
        logger.debug("Encoding password for user " + createUserRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        logger.debug("User Creation successful for user " + createUserRequest.getUsername());
        return ResponseEntity.ok(user);
    }

}
