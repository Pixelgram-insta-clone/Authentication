package com.cognizant.Auth.service;

import com.cognizant.Auth.exception.InvalidCredentialsException;
import com.cognizant.Auth.exception.UserAlreadyExistsException;
import com.cognizant.Auth.exception.UserNotFoundException;
import com.cognizant.Auth.model.ResponseDto;
import com.cognizant.Auth.model.User;
import com.cognizant.Auth.model.UserDto;

public interface AuthService {

    public ResponseDto validateUser(UserDto userDto) throws InvalidCredentialsException, UserAlreadyExistsException, UserNotFoundException;
    public ResponseDto registerUser(UserDto userDto) throws  UserAlreadyExistsException;
    public User sendToUserCrud(ResponseDto dto);
}
