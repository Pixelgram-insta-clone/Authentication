package com.cognizant.Auth.controller;

import com.cognizant.Auth.exception.InvalidCredentialsException;
import com.cognizant.Auth.exception.UserAlreadyExistsException;
import com.cognizant.Auth.exception.UserNotFoundException;
import com.cognizant.Auth.model.ResponseDto;
import com.cognizant.Auth.model.UserDto;
import com.cognizant.Auth.service.AuthServiceImpl;
import com.cognizant.Auth.util.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.sql.Date;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTests {

    @Mock
    AuthServiceImpl authService;

    @InjectMocks
    AuthController authController;

    private static JWTService jwtService;
    private MockMvc mockMvc;

    private UserDto userDto;
    private ResponseDto responseDto;
    private String json;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.authController).addPlaceholderValue("tmem-ui", "http://localhost:4200").build();
        jwtService = JWTService.getInstance();
    }


    @BeforeEach
    void init() throws JsonProcessingException {
        userDto = new UserDto("GB0Y", "password🔥");
        responseDto = new ResponseDto(1, "GB0Y");
        json = objectMapper.writeValueAsString(userDto);
    }

    @Test
    void testValidLogin_positive() throws Exception {
        // Arrange
        when(authService.validateUser(userDto)).thenReturn(responseDto);

        mockMvc.perform(post("/login")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testInvalidLoginExistingUser_negative() throws Exception {
        when(authService.validateUser(userDto)).thenThrow(InvalidCredentialsException.class);

        mockMvc.perform(post("/login")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidLoginUserDoesNotExist_negative() throws Exception {
        when(authService.validateUser(userDto)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/login")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_Positive() throws Exception{
        when(authService.registerUser(userDto)).thenReturn(responseDto);

        mockMvc.perform(post("/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRegister_Negitive() throws Exception{
        when(authService.registerUser(userDto)).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(post("/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testIsValidToken_positive() throws Exception{
        // Arrange
        String token = jwtService.generateToken(responseDto);

        // Act and Assert
        mockMvc.perform(post("/authenticate")
                .content(token))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void testIsInvalidToken_negative() throws Exception {
        String token = jwtService.generateToken(responseDto);
        String invalidToken = jwtService.setExpiration(token, Date.from(Instant.now()));

        mockMvc.perform(post("/authenticate")
                .content(invalidToken))
                .andExpect(status().isBadRequest());
    }

}
