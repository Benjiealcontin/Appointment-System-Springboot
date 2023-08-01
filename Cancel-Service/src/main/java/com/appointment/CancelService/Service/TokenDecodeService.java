package com.appointment.CancelService.Service;

import com.appointment.AppointmentService.Exception.InvalidTokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

@Service
public class TokenDecodeService {

    public String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Bearer token");
        }
        return bearerToken.substring(7); // Extract token excluding "Bearer "
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