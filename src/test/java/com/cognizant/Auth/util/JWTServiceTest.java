package com.cognizant.Auth.util;

import com.cognizant.Auth.model.ResponseDto;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JWTServiceTest {

    // AAA
    // Arrange

    private static ResponseDto dto;
    private static JWTService jwtService;
    private static String token;

    @BeforeAll
    static void init() {

        jwtService = JWTService.getInstance();
        dto = new ResponseDto(1, "jimmy");
    }

    @Test
    @Order(1)
    void test_generateValidJwtToken_positive() {
        // Act
        token = jwtService.generateToken(dto);
        String actual = token.split("\\.")[0];
        String expected = jwtService.generateToken(dto).split("\\.")[0];

        // Assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    void test_parseValidJwtToken_positive() {
        // Act
//        String token = jwtService.generateToken(dto);
        Boolean actual = jwtService.parseToken(token);
        // Assert
        Assertions.assertTrue(actual);
    }

    @Test
    @Order(3)
    void test_parseInvalidJwtToken_negative() {

        Boolean actual = jwtService.parseToken(token.substring(0, token.length() - 2));

        Assertions.assertFalse(actual);

    }

    @Test
    @Order(4)
    void test_checkSetExpiration_positive(){
        String token = jwtService.generateToken(dto);
        Date newDate = Date.from(Instant.now().plus(11, ChronoUnit.DAYS));
        String newToken = jwtService.setExpiration(token, newDate);
        Assertions.assertNotEquals(token, newToken);
    }
}
