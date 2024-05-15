package com.website.blogs.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public Authentication authenticate (String username, String password) throws BadCredentialsException, NotFoundException {
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new NotFoundException("Пользователь не найден!");
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        userDetails.getAuthorities()
                )
        );
    }
}
