package com.website.blogs;

import com.website.blogs.entity.User;
import com.website.blogs.repository.RoleRepository;
import com.website.blogs.repository.UserRepository;
import com.website.blogs.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class RegistrationTests {

    @InjectMocks
    private static UserService userService;
    private static RoleRepository roleRepository;
    private static UserRepository userRepository;

    @BeforeAll
    public static void init() {
        // Init mocks & service to test
        roleRepository = Mockito.mock(RoleRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        userService = new UserService(
                userRepository, roleRepository
        );
    }

    @Test
    public void success_save_user() {
        // arrange
        // default user without any fields
        User user = new User();
        user.setUsername("test-good-user");
        user.setPassword("test-password");
        user.setEmail("testEmail@email.com");

        // act
        String message = userService.saveUser(user);

        // assert
        Assertions.assertEquals("Пользователь успешно сохранен!", message);
    }

    @Test
    public void save_empty_user() {
        // arrange
        User user = new User(); // empty user

        // act
        String message = userService.saveUser(user);

        // assert
        Assertions.assertEquals("Все поля должны быть заполнены!", message);
    }

    @Test
    public void user_with_incorrect_email() {
        // arrange
        User user = new User();
        user.setUsername("test-user-3");
        user.setEmail("alsjdfalsdfd"); // - incorrect email
        user.setPassword("test-password");

        // act
        String message = userService.saveUser(user);

        // assert
        Assertions.assertEquals("Введите корректную почту!", message);
    }



}
