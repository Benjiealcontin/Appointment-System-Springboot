package com.appointment.Approve_Service.Service;



import com.appointment.Approve_Service.Exception.InvalidTokenException;
import com.appointment.Approve_Service.Request.UserTokenData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;


@Service
public class TokenDecodeService {

    public String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Bearer token");
        }
        return bearerToken.substring(7);
    }

    //TODO: Address
    public UserTokenData decodeUserToken(String token) {
        if (token != null) {
            // Decode the JWT token
            DecodedJWT decodedJWT = JWT.decode(token);

            // Extract user information from the JWT payload
            String givenName = decodedJWT.getClaim("given_name").asString();
            String familyName = decodedJWT.getClaim("family_name").asString();
            String phoneNumber = decodedJWT.getClaim("phone_number").asString();
            String email = decodedJWT.getClaim("email").asString();
            String gender = decodedJWT.getClaim("gender").asString();
            Integer ageClaim = decodedJWT.getClaim("age").asInt();
            int age = ageClaim != null ? ageClaim : 0; // Provide a default value if ageClaim is null
            String sub = decodedJWT.getSubject();

            return new UserTokenData(givenName, familyName, phoneNumber, email, sub, gender, age);
        } else {
            throw new IllegalArgumentException("Token is null");
        }
    }
}