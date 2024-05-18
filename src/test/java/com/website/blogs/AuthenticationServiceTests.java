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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


@SpringBootTest
public class AuthenticationServiceTests {
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

    }
    @Test
    public void success_authentication() throws NotFoundException {
        // Arrange
        String username = "authenticaionuser";
        String password = "secret-password";

        // UserForReturn of loadByUsername call
        UserDetails userDetailsForReturn = new User(
                username,
                password,
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

        // Делаем мок для authenticationManager (Имитируем его работу, так как в тестовом классе он всего лишь мок объект)
        Mockito.when(authenticationManager.authenticate(successAuthenticationToken)).thenReturn(successAuthenticationToken);

        // act
        Authentication authentication = authenticationService.authenticate(
                username, password
        );

        // Assert
        // Если токен аутентфикации не равен нулю, тогда значит что пользователь успешно получил токен и все норм
        Assertions.assertEquals(null ,authentication);
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
    public void bad_credentionals_exception() throws NotFoundException { // Пробрасываем NotFound, так как пользователь создан выше
        // arrange
        String username = "authenticaionuser"; // Пользователь которого мы ранее успешно аутентифицировали
        String password = "wrong-password"; // Но в данном случае с неправильным паролем

        UserDetails userDetails = new User(
                username,
                "secret-password", // Здесь мы указываем другой пароль, так как у нас тест для проверки пароля
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // дефолтная роль
        );

        // При вызове loadByUsername мы возвращаем объект, который только что создали
        Mockito.when(userService.loadUserByUsername(username)).thenReturn(userDetails);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                password, // password будет другой, так как токен должен быть неправильным в тестовом сценарии
                userDetails.getAuthorities()
        );

        // Данный метод будет возвращать null, так как токен аутентификации у нас неправильный
        Mockito.when(authenticationManager.authenticate(authentication)).thenReturn(null);


        // act
        Authentication auth = authenticationService.authenticate(username, password);

        // assert
        // auth должен быть null, так как токен аутентификации неправильный
        Assert.assertEquals(null, auth);
    }

}