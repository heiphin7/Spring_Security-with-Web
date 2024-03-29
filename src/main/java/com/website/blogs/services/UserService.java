package com.website.blogs.services;


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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<User> getCurrentUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = findByUsername(authentication.getName());

        return optionalUser;
    }

    public void saveUser(User user){
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").orElseThrow()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean userExists(String username){
        return findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(
                () ->
                        new UsernameNotFoundException(
                                String.format("Пользователь %s е найден", username)
                        )
        );
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
