package com.cognizant.Auth.proxy;

import com.cognizant.Auth.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProxyTest {

    public static WireMockServer wireMockServer;

    private User user = new User(1, "user1", "profileimg.png");
    private String userJSON;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserProxy proxy;

    @BeforeAll
    void setup() {
        wireMockServer = new WireMockServer(options().port(8090));
        wireMockServer.start();
    }

    @BeforeEach
    void init() throws JsonProcessingException {
        userJSON = objectMapper.writeValueAsString(user);
    }

    @Test
    void test_userProxyReturnsUser_positive(){
        wireMockServer.stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(userJSON)));

        String response = "";

        try {
            response = objectMapper.writeValueAsString(proxy.getUserById(1));

        } catch (JsonProcessingException e){
            fail();
        }
        assertThat(response, is(userJSON));
    }

    @Test
    void test_userProxyNoUserFound_negative(){
        wireMockServer.stubFor(get(urlEqualTo("/users/0"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new String())));

        User response = proxy.getUserById(0);

        assertNull(response);
    }
}
