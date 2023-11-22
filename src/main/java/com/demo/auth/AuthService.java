package com.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.entidad.Role;
import com.demo.entidad.User;
import com.demo.entidad.UserRepository;
import com.demo.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    // private final UserRepository userRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                                        (request.getUserName(), request.getPassword()));
    
            UserDetails user = userRepository.findByUserName(request.getUserName())
                                .orElseThrow();                           
            
            String token = jwtService.getToken(user);                    
            return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        
        User user = User.builder()
            .userName(request.getUserName())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .country(request.getCountry())
            .role(Role.USER)
            .build();
           
        userRepository.save(user);
        
        return AuthResponse.builder()
            .token(jwtService.getToken(user))
            .build();
    }

}
