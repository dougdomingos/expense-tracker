package com.dougdomingos.expensetracker.auth;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.dougdomingos.expensetracker.entities.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final JwtEncoder jwtEncoder;

    /**
     * Generates a JWT for a given user and expiration time.
     * 
     * @param user      The user for which the token will be created
     * @param expiresIn Expiration time for the token (in seconds)
     * @return The generated token
     */
    public String generateToken(User user, long expiresIn) {
        Instant now = Instant.now();
        String scopes = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("expense-tracker")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
