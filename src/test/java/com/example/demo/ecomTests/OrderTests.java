package com.example.demo.ecomTests;

import com.example.demo.model.persistence.entities.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class OrderTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    Logger logger = LoggerFactory.getLogger(OrderTests.class);

    private static final String USERNAME = "akshay";
    private static final String PASSWORD = "password123";


    @Before
    public void setup() throws Exception {
        logger.debug("Setting up login for UserTests");

        CreateUserRequest user = new CreateUserRequest(USERNAME, PASSWORD, PASSWORD);

        User myUser = userRepository.findByUsername(user.getUsername());

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
    }

    @Test
    public void submit() throws Exception {
        mvc.perform(
                        post("/api/order/submit/".concat(USERNAME))
                                .headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrdersForUser() throws Exception {
        mvc.perform(
                        get("/api/order/history/".concat(USERNAME))
                                .headers(httpHeaders))
                .andExpect(status().isOk());
    }
}
