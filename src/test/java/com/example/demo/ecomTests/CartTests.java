package com.example.demo.ecomTests;

import com.example.demo.model.persistence.entities.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CartTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext wac;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    Logger logger = LoggerFactory.getLogger(CartTests.class);

    private static final String USERNAME = "akshay";
    private static final String PASSWORD = "password123";


    /**
     * Creates pre-requisites for testing.
     */
    @Before
    public void setup() throws Exception {

        logger.debug("In loginSetup() logging in user ");
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
    public void addToCart() throws Exception {
        ModifyCartRequest body = new ModifyCartRequest();
        body.setUsername(USERNAME);
        body.setItemId(1);
        body.setQuantity(2);
        mvc.perform(
                        post("/api/cart/addToCart")
                                .headers(httpHeaders)
                                .content(mapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void removeFromCart() throws Exception {
        ModifyCartRequest body = new ModifyCartRequest();
        body.setUsername(USERNAME);
        body.setItemId(1);
        body.setQuantity(1);
        mvc.perform(
                        post("/api/cart/removeFromCart")
                                .headers(httpHeaders)
                                .content(mapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
