package com.appointment.AppointmentService.Service;

import com.appointment.AppointmentService.Exception.InvalidTokenException;
import com.appointment.AppointmentService.Request.Patient;
import com.appointment.AppointmentService.Request.UserTokenData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.net.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class TokenDecodeService {

    private final WebClient.Builder webClientBuilder;

    public TokenDecodeService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }



    public String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Bearer token");
        }
        return bearerToken.substring(7);
    }

    public UserTokenData decodeUserToken(String token,String bearerToken) {
        try {
        DecodedJWT decodedJWT = JWT.decode(token);

        String sub = decodedJWT.getSubject();

        Patient patient = webClientBuilder.build()
                .get()
                .uri("http://Keycloak-Service/api/keycloak/getPatient/{sub}", sub)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();

        assert patient != null;
        Map<String, Object> patientAttributes = patient.getAttributes();
        Object locationValue = patientAttributes.get("street");
        String street = locationValue.toString();
        String streetAddress = street.replaceAll("[\\[\\]]", "");

        Object localityValue = patientAttributes.get("locality");
        String localityAddress = localityValue.toString();
        String locality = localityAddress.replaceAll("[\\[\\]]", "");

        Object regionValue = patientAttributes.get("region");
        String regionAddress = regionValue.toString();
        String region = regionAddress.replaceAll("[\\[\\]]", "");

        Object postal_codeValue = patientAttributes.get("postal_code");
        String postal_codeAddress = postal_codeValue.toString();
        String postalCode = postal_codeAddress.replaceAll("[\\[\\]]", "");

        Object countryValue = patientAttributes.get("country");
        String countryAddress = countryValue.toString();
        String country = countryAddress.replaceAll("[\\[\\]]", "");

        String givenName = patient.getFirstName();
        String familyName = patient.getLastName();
        String email = patient.getEmail();

        Object phoneNumberValue = patientAttributes.get("phoneNumber");
        String phoneNumberOfPatient = phoneNumberValue.toString();
        String phoneNumber = phoneNumberOfPatient.replaceAll("[\\[\\]]", "");

        Object genderValue = patientAttributes.get("gender");
        String genderOfPatient = genderValue.toString();
        String gender = genderOfPatient.replaceAll("[\\[\\]]", "");

//        List<String> contactInformation = (List<String>) patientAttributes.get("contactInformation");
//        String email = contactInformation.get(0);
//        String phoneNumber = contactInformation.get(1);

        Object ageValue = patientAttributes.get("age");
        String ageString = ageValue.toString();
        ageString = ageString.replaceAll("[\\[\\]]", "");

        int age = Integer.parseInt(ageString);

        return new UserTokenData(givenName, familyName, phoneNumber, email, age, sub, gender, streetAddress , locality,
                region, postalCode, country);

        } catch (JWTDecodeException jwtDecodeException) {
            throw new RuntimeException("Error decoding JWT token.", jwtDecodeException);
        } catch (WebClientResponseException webClientResponseException) {
            throw new RuntimeException("Error retrieving patient data.", webClientResponseException);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred.", e);
        }
    }


//    public UserTokenData decodeUserToken(String token) throws JSONException {
//        if (token != null) {
//            // Decode the JWT token
//            DecodedJWT decodedJWT = JWT.decode(token);
//
//            // Extract user information from the JWT payload
//            String givenName = decodedJWT.getClaim("given_name").asString();
//            String familyName = decodedJWT.getClaim("family_name").asString();
//            String phoneNumber = decodedJWT.getClaim("phone_number").asString();
//            String email = decodedJWT.getClaim("email").asString();
//            String gender = decodedJWT.getClaim("gender").asString();
//            Integer ageClaim = decodedJWT.getClaim("age").asInt();
//            int age = ageClaim != null ? ageClaim : 0; // Provide a default value if ageClaim is null
//            String sub = decodedJWT.getSubject();
//
//
//            return new UserTokenData(givenName, familyName, phoneNumber, email, sub, gender, age);
//        } else {
//            throw new IllegalArgumentException("Token is null");
//        }
//    }
}