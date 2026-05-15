package com.boilerplate.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties;
    private UserDetails userDetails;

    // Minimum 256-bit secret for HS256
    private static final String TEST_SECRET =
        "test-secret-key-that-is-long-enough-for-hs256-algorithm-at-least-32-chars";
    private static final long ACCESS_TTL = 900_000L;      // 15 minutes
    private static final long REFRESH_TTL = 2_592_000_000L; // 30 days
    private static final long REMEMBER_ME_TTL = 7_776_000_000L; // 90 days

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(TEST_SECRET);
        jwtProperties.setAccessTokenExpiration(ACCESS_TTL);
        jwtProperties.setRefreshTokenExpiration(REFRESH_TTL);
        jwtProperties.setRememberMeExpiration(REMEMBER_ME_TTL);

        jwtService = new JwtService(jwtProperties);

        userDetails = new User(
            "testuser",
            "password",
            List.of(new SimpleGrantedAuthority("USER_READ"), new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void generateAccessToken_ReturnsNonNullToken() {
        String token = jwtService.generateAccessToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        String token = jwtService.generateAccessToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateAccessToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_WithWrongUser_ReturnsFalse() {
        String token = jwtService.generateAccessToken(userDetails);

        UserDetails otherUser = new User("otheruser", "password", List.of());
        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_WithMalformedToken_ReturnsFalse() {
        boolean valid = jwtService.isTokenValid("not.a.valid.jwt", userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    void generateRefreshToken_WithoutRememberMe_UsesRefreshTtl() {
        String token = jwtService.generateRefreshToken(userDetails, false);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void generateRefreshToken_WithRememberMe_UsesRememberMeTtl() {
        String token = jwtService.generateRefreshToken(userDetails, true);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void differentUsers_ProduceDifferentTokens() {
        UserDetails otherUser = new User("otheruser", "pass", List.of());

        String token1 = jwtService.generateAccessToken(userDetails);
        String token2 = jwtService.generateAccessToken(otherUser);

        assertThat(token1).isNotEqualTo(token2);
    }
}
