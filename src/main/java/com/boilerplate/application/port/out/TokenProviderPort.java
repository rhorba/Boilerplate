package com.boilerplate.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenProviderPort {
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);
}
