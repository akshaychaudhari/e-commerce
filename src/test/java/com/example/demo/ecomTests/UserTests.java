package com.example.demo.ecomTests;


import com.example.demo.model.persistence.entities.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginUserRequest;
import com.example.demo.util.TestLoginUtil;
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
public class UserTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    Logger logger = LoggerFactory.getLogger(CartTests.class);

    private HttpHeaders httpHeaders = new HttpHeaders();

    public static final String USERNAME = "akshay";
    public static final String PASSWORD = "password123";

    /**
     * Creates pre-requisites for testing.
     */
    @Before
    public void setup() throws Exception {

        logger.debug("Setting up login for UserTests");
        /*TestLoginUtil testLoginUtil = new TestLoginUtil();
        httpHeaders = testLoginUtil.login();*/
        CreateUserRequest user = new CreateUserRequest(USERNAME, PASSWORD, PASSWORD);

        User myUser = userRepository.findByUsername(user.getUsername());

        String endpoint = Objects.isNull(myUser) ? "create" : "login";

        if(endpoint.equals("create")){
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
    public void testUserById() throws Exception {
        mvc.perform(get("/api/user/id/1").headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserByName() throws Exception {
        mvc.perform(get("/api/user/".concat(TestLoginUtil.USERNAME)).headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserByInvalidId() throws Exception {
        mvc.perform(get("/api/user/id/999").headers(httpHeaders))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUserByInvalidUserName() throws Exception {
        mvc.perform(get("/api/user/InvalidUser").headers(httpHeaders))
                .andExpect(status().isNotFound());
    }


}
