package com.website.blogs;

/*
* by @heiphin7
*/

import com.website.blogs.repository.RoleRepository;
import com.website.blogs.repository.UserRepository;
import com.website.blogs.services.AuthenticationService;
import com.website.blogs.services.UserService;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


@SpringBootTest
public class AuthenticationTests {
    // Инициализация mockBean-ов
    @Mock
    private static UserRepository userRepository;
    @Mock
    private static RoleRepository roleRepository;
    @Mock
    private static AuthenticationManager authenticationManager;
    @InjectMocks
    private static UserService userService;
    @InjectMocks
    private static AuthenticationService authenticationService;

    @BeforeEach
    public void init() {
        // Инициализируем моки, которые используются в authenticationService
        roleRepository = Mockito.mock(RoleRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userService = Mockito.mock(UserService.class);

        authenticationService = new AuthenticationService(
                authenticationManager, userService
        );

        // UserForReturn of loadByUsername call
        UserDetails userDetailsForReturn = new User(
                "authenticaionuser",
                "secret-password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // Роль по умолчанию
        );

        // Имитируем поведение мока UserService которое возвращает пользователя по имени
        Mockito.when(userService.loadUserByUsername("authenticaionuser")).thenReturn(userDetailsForReturn);

        UsernamePasswordAuthenticationToken successAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetailsForReturn.getUsername(),
                        userDetailsForReturn.getPassword(),
                        userDetailsForReturn.getAuthorities()
                );

       /* AuthenticationManager.Authenticate будет принимать authentication и также его возвращать.
        * Если переданный токен будет неправильным, тогда он выкинет исключение BadCredentionalException
        * В данном случае мы только имитируем его работу, поэтому предпологается что authenticationToken правильный                                                             */

        // for success authentication
         Mockito.when(authenticationManager.authenticate(successAuthenticationToken)).thenReturn(successAuthenticationToken);


    }
    @Test
    public void success_authentication() throws NotFoundException {
        // Arrange
        String username = "authenticaionuser";
        String password = "secret-password";

        // act
        Authentication authentication = authenticationService.authenticate(
                username, password
        );

        // Assert
        // Если токен аутентфикации не равен нулю, тогда значит что пользователь успешно получил токен и все норм
        Assertions.assertNotNull(authentication);
    }

    @Test
    public void username_notFound_exception() {
        // Arrange
        String username = "askdfjnalkejbfiaoefapwiojdfa"; // Несуществующий пользователь
        String password = "some-password";

        // Assert & throw exception
        Assert.assertThrows(NotFoundException.class, () -> {
            authenticationService.authenticate(username, password);
        });
    }

    @Test
    public void bad_credentionals_exception() {
        // arrange
        String username = "authenticaionuser";
        String password = "wrong-password";

        // act & assert
        Assert.assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(username, password);
        });
    }

}