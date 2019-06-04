package security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.DTOs.UserDto;
import models.UserDetails;
import models.UserModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        response.addHeader("Authorization", "Token " + token);
        UserDto user = new UserDto(userDetails);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String userJson = mapper.writeValueAsString(user);
        response.getWriter().write(userJson);
    }
}

