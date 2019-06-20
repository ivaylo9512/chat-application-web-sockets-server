package com.chat.app.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private byte[] encodedBytes = Base64.getEncoder().encode(Jwt.jwtSecret.getBytes());

    public AuthenticationFilter() {
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException {
        try {
            UserModel userModel = new ObjectMapper().readValue(request.getInputStream(), UserModel.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userModel.getUsername(),
                    userModel.getPassword(), new ArrayList<>()));

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication auth) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = Jwt.generate(userDetails);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getId()));
        response.addHeader("Authorization", "Token " + token);
        chain.doFilter(request, response);
    }
}

