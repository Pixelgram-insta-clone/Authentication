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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    AuthRepository authRepository;

    @Autowired
    UserProxy userProxy;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseDto validateUser(UserDto userDto) throws InvalidCredentialsException, UserNotFoundException {

        Optional<UserCredential> userCredential = authRepository.findByUsername(userDto.getUsername());

        if(userCredential.isPresent()) {
            UserCredential user = userCredential.get();

            if(userProxy.getUserById(user.getId()) == null) {
                throw new UserNotFoundException("User not found in UserDB.");
            }
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            if(bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                return modelMapper.map(user, ResponseDto.class);
            } else {
                throw new InvalidCredentialsException("Invalid user credentials.");
            }

        }
        throw new UserNotFoundException("User not found in AuthDB.");
    }

    @Override
    public ResponseDto registerUser(UserDto userDto) throws UserAlreadyExistsException {
        Optional<UserCredential> userCredential = authRepository.findByUsername(userDto.getUsername());

        if(userCredential.isPresent()) {
            throw new UserAlreadyExistsException("Username Already Taken");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserCredential newUser = authRepository.save(new UserCredential(userDto));

        ResponseDto responseDto = modelMapper.map(newUser, ResponseDto.class);
        sendToUserCrud(responseDto);

        return responseDto;
    }
    @Override
    public User sendToUserCrud(ResponseDto dto) {
        return userProxy.registerNewUser(dto);
    }
}
