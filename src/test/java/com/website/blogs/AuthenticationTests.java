package com.website.blogs;

import com.website.blogs.services.AuthenticationService;
import com.website.blogs.services.UserService;
import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


@SpringBootTest
@RunWith(JUnit4.class)
public class AuthenticationTests {
    @InjectMocks
    private static AuthenticationService authenticationService;
    private static AuthenticationManager authenticationManager;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        // init mocks & service for testing
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userService = Mockito.mock(UserService.class);

        authenticationService = new AuthenticationService(
                authenticationManager, userService
        );
    }


    @Test
    public void success_authentication() throws NotFoundException {
        // Arrange
        String username = "authenticationuser";
        String password = "secret-password";

        // act

        Authentication authentication = authenticationService.authenticate(
                username, password
        );

        // Assert
        Assertions.assertNotNull(authentication);
    }

}