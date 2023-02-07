package com.cognizant.Auth.service;


import com.cognizant.Auth.exception.InvalidCredentialsException;
import com.cognizant.Auth.exception.UserAlreadyExistsException;
import com.cognizant.Auth.exception.UserNotFoundException;
import com.cognizant.Auth.model.ResponseDto;
import com.cognizant.Auth.model.User;
import com.cognizant.Auth.model.UserCredential;
import com.cognizant.Auth.model.UserDto;
import com.cognizant.Auth.proxy.UserProxy;
import com.cognizant.Auth.repository.AuthRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private UserCredential userCredential;
    private UserDto dto;
    private ResponseDto responseDto;
    private User user;




    @Mock
    AuthRepository authRepo;

    @Mock
    UserProxy userProxy;

    @Spy
    ModelMapper modelMapper;

    @Spy
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    AuthServiceImpl authService;

    @BeforeEach
    void init() {

        userCredential = new UserCredential(1, "jimmy", "neutron");
        dto = new UserDto("jimmy", "neutron");
        responseDto = new ResponseDto(1, "jimmy");
        user = new User(1, "jimmy", "profile_img.jpg");
    }

    // Controller(req endpoint "/authenticate") -> Service -> Repo validate
    // Service -> Controller(response) -> JwtService -> LoginUI

    @Test
    void test_validateUser_positive() throws Exception {
        // Arrange
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(userCredential));
        when(userProxy.getUserById(userCredential.getId())).thenReturn(user);

        userCredential.setPassword(bCryptPasswordEncoder.encode(userCredential.getPassword()));

        System.out.println(bCryptPasswordEncoder.encode(userCredential.getPassword()));
        // Act
        ResponseDto actual = authService.validateUser(dto);

        // Assert
        // expected == actual
        Assertions.assertEquals(responseDto, actual);
    }

    @Test
    void test_validateUserThatDoesNotExist_negative() {
        // Arrange
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());

        // Act & Assert [When throwing Exceptions]
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            authService.validateUser(dto);
        });
    }

    @Test
    void test_validateUserExistingWithInvalidPassword_negative() {
        //Arrange
        dto.setPassword("wrongpass");
        when(userProxy.getUserById(userCredential.getId())).thenReturn(user);
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(userCredential));

        //Act and Assert
        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            authService.validateUser(dto);
        });
    }

    @Test
    void test_validateUserExistsInAuthDBNonExistentUserDB_negative() {
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(userCredential));
        when(userProxy.getUserById(userCredential.getId())).thenReturn(null);

        Assertions.assertThrows(UserNotFoundException.class, ()->{
            authService.validateUser(dto);
        });
    }

    @Test
    void test_checkIfUsernameDoesNotExist_positive() throws InvalidCredentialsException, UserAlreadyExistsException {
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(authRepo.save(any(UserCredential.class))).thenReturn(userCredential);


        ResponseDto actual = authService.registerUser(dto);

        Assertions.assertEquals(responseDto, actual);
    }

    @Test
    void test_checkIfUsernameDoesNotExist_negative() {
        when(authRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(userCredential));
        userCredential.setId(0);

        Optional<UserCredential> actual = authRepo.findByUsername(dto.getUsername());

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerUser(dto);
        });
    }


    @Test
    void test_checkIfGetCrudResponse_Positive() {
        when(userProxy.registerNewUser(responseDto)).thenReturn(user);

        User actual = authService.sendToUserCrud(responseDto);

        Assertions.assertEquals(user, actual);

    }

    @Test
    void test_checkIfGetCrudResponse_Negative() {
        when(userProxy.registerNewUser(responseDto)).thenReturn(null);

        User actual = authService.sendToUserCrud(responseDto);

        Assertions.assertNull(actual);
    }

    @Test
    void hashPassword_positive() throws UserAlreadyExistsException {
        String actual = bCryptPasswordEncoder.encode(dto.getPassword());
        Assertions.assertTrue(bCryptPasswordEncoder.matches(dto.getPassword(), actual));
    }

    @Test
    void hashPassword_negitive() throws UserAlreadyExistsException {
//        when(authService.registerUser(dto)).thenReturn(bCryptPasswordEncoder.encode(dto.getPassword()));
        String actual = bCryptPasswordEncoder.encode(dto.getPassword());
        Assertions.assertFalse(bCryptPasswordEncoder.matches("Wrong Password", actual));
    }
}
