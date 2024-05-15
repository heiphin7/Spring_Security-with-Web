package com.website.blogs.services;


import com.website.blogs.dtos.RegistrationUserDTO;
import com.website.blogs.entity.Role;
import com.website.blogs.entity.User;
import com.website.blogs.repository.RoleRepository;
import com.website.blogs.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void changePassword(User user, String newEncodedPassword) {
        // Устанавливаем новый пароль
        user.setPassword(newEncodedPassword);

        // Сохраняем пользователя но уже с новым паролем
        userRepository.save(user);
    }

    public Optional<User> getCurrentUser () {
        return findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public String saveUser(User user){
        String username = user.getUsername();

        // Проверяем все поля
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "Все поля должны быть заполнены!";
        }

        if(userExists(username)){
            return "Пользователь с таким именем уже сущесвует!";
        }

        if (!user.getEmail().matches("^[^\\s]+$") || !user.getEmail().matches(".*@.*")) {
            return "Введите корректную почту!";
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER").orElse(null);

        if(defaultRole == null) {
            defaultRole = new Role();
            defaultRole.setId(1L);
            defaultRole.setName("ROLE_USER");
            roleRepository.save(defaultRole);
        }

        user.setRoles(List.of(defaultRole));
        userRepository.save(user);
        return "Пользователь успешно сохранен!";
    }


    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean userExists(String username)  {
        return findByUsername(username).isPresent();
    }

    public void updateFavoriteBlogs(User originalUser, User updatedUser) {
        originalUser.setFavoriteBlogs(updatedUser.getFavoriteBlogs());
        userRepository.save(originalUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    // Метод для проверки, сущесвтует ли email
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Для Spring Security, Метод для UserDetails
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow
                (() -> new UsernameNotFoundException(
                                String.format("Пользователь %s найден", username)
                        ));

        int size = user.getRoles().size();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),

                    user.getRoles().stream().map(
                            role -> new SimpleGrantedAuthority(role.getName())
                    ).collect(Collectors.toList())
            );
    }

}
