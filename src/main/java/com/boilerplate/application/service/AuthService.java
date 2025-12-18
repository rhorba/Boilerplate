package com.boilerplate.application.service;

import com.boilerplate.application.dto.AuthenticationRequest;
import com.boilerplate.application.dto.AuthenticationResponse;
import com.boilerplate.application.dto.RegisterRequest;
import com.boilerplate.domain.exception.RoleNotFoundException;
import com.boilerplate.domain.exception.UserAlreadyExistsException;
import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.application.port.out.TokenProviderPort;
import com.boilerplate.domain.port.out.RoleRepository;
import com.boilerplate.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private static final String DEFAULT_ROLE = "USER";

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final TokenProviderPort tokenProvider;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;
        private final ActivityLogService activityLogService;
        private final RoleRepository roleRepository;

        public AuthenticationResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new UserAlreadyExistsException("Email already exists");
                }

                Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                                .orElseThrow(() -> new RoleNotFoundException(
                                                "Default role " + DEFAULT_ROLE + " not found"));

                var user = User.builder()
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(userRole)
                                .build();

                userRepository.save(user);
                activityLogService.log("REGISTER", "New user registered: " + user.getEmail(), user.getEmail());

                // Load UserDetails to generate token (using the UserDetailsService which
                // usually returns our Entity implementing UserDetails)
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                var jwtToken = tokenProvider.generateToken(userDetails);
                var refreshToken = tokenProvider.generateRefreshToken(userDetails);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var userDetails = userDetailsService.loadUserByUsername(request.getEmail());

                activityLogService.log("LOGIN", "User logged in", request.getEmail());

                var jwtToken = tokenProvider.generateToken(userDetails);
                var refreshToken = tokenProvider.generateRefreshToken(userDetails);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public User getCurrentUser() {
                var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                                .getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new RuntimeException("User not authenticated");
                }
                var email = authentication.getName();
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }
}
