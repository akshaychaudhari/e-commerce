package com.example.demo.util;

import com.example.demo.model.persistence.entities.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Component
public class TestLoginUtil {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public TestLoginUtil() {
    }

    public static final String USERNAME = "akshay";
    public static final String PASSWORD = "password123";
    //public final HttpHeaders httpHeaders = new HttpHeaders();

    Logger logger = LoggerFactory.getLogger(TestLoginUtil.class);


    public HttpHeaders login() throws Exception {

        logger.debug("In login() logging in user " + USERNAME);

        HttpHeaders httpHeaders = new HttpHeaders();

        CreateUserRequest user = new CreateUserRequest(USERNAME, PASSWORD, PASSWORD);

        User myUser = null;
        try {
            myUser = userRepository.findByUsername(USERNAME);
        } catch (Exception e) {
            logger.debug("New user");
        }

        String endpoint = Objects.isNull(myUser) ? "create" : "login";

        if (endpoint.equals("create")) {
            mvc.perform(
                    post("/api/user/".concat(endpoint))
                            .content(mapper.writeValueAsString(user))
                            .contentType(MediaType.APPLICATION_JSON));

        }

        LoginUserRequest userRequest = new LoginUserRequest(USERNAME, PASSWORD);
        MvcResult response = mvc.perform(
                        post("/login")
                                .content(mapper.writeValueAsString(userRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logger.debug("In loginSetup() Auth Header is " + response.getResponse().getHeader("Authorization"));

        httpHeaders.set(HttpHeaders.AUTHORIZATION, response.getResponse().getHeader("Authorization"));

        return httpHeaders;
    }

}
