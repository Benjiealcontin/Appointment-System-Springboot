package com.appointment.CancelService.Controller;

import com.appointment.CancelService.Response.MessageResponse;
import com.appointment.CancelService.Service.CancelService;
import com.appointment.CancelService.Service.TokenDecodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/api/cancel")
public class CancelController {

    private final CancelService cancelService;

    private final TokenDecodeService tokenService;
    public CancelController(CancelService cancelService, TokenDecodeService tokenService) {
        this.cancelService = cancelService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> cancelAppointment(@PathVariable String transactionId, @RequestHeader("Authorization")String bearerToken) {
        try {
            String token = tokenService.extractToken(bearerToken);
            String subject = tokenService.decodeSubjectFromToken(token);
            MessageResponse response = cancelService.cancelAppointment(transactionId, subject, bearerToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("An error occurred: " + e.getMessage());
        } catch (WebClientResponseException e) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("An error occurred: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }
}
