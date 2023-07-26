package com.appointment.AppointmentService.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;


@Service
public class TokenDecodeService {

    public String extractToken(String bearerToken) {
        // Assuming the Authorization header looks like "Bearer <token>"
        String[] tokenParts = bearerToken.split(" ");
        if (tokenParts.length == 2 && "Bearer".equals(tokenParts[0])) {
            return tokenParts[1];
        } else {
            throw new IllegalArgumentException("Invalid Bearer token");
        }
    }


    public String decodeSubjectFromToken(String token) {
        if (token != null) {
            // Decode the JWT token
            DecodedJWT decodedJWT = JWT.decode(token);

            return decodedJWT.getSubject();
        } else {
            throw new IllegalArgumentException("Token is null");
        }
    }
}
