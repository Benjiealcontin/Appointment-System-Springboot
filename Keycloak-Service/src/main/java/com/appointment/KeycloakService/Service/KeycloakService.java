package com.appointment.KeycloakService.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KeycloakService {


    public String extractToken(String bearerToken) {
        // Assuming the Authorization header looks like "Bearer <token>"
        String[] tokenParts = bearerToken.split(" ");
        if (tokenParts.length == 2 && "Bearer".equals(tokenParts[0])) {
            return tokenParts[1];
        } else {
            throw new IllegalArgumentException("Invalid Bearer token");
        }
    }


    public void decodeAndPrintToken(String token) {
        if (token != null) {
            // Decode the JWT token
            DecodedJWT decodedJWT = JWT.decode(token);

            // Extract information from the token
            String subject = decodedJWT.getSubject();
            Date expiration = decodedJWT.getExpiresAt();
            // You can extract other claims or information from the token as needed.

            System.out.println("Subject: " + subject);
            System.out.println("Expiration: " + expiration);
        }
    }
}
