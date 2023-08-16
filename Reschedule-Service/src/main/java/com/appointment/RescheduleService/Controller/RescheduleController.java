package com.appointment.RescheduleService.Controller;

import com.appointment.RescheduleService.Request.RescheduleRequest;
import com.appointment.RescheduleService.Response.MessageResponse;
import com.appointment.RescheduleService.Service.RescheduleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reschedule")
public class RescheduleController {

    private final RescheduleService rescheduleService;

    public RescheduleController(RescheduleService rescheduleService) {
        this.rescheduleService = rescheduleService;
    }

    @PostMapping("/{transactionId}")
    public MessageResponse reschedule(@PathVariable String transactionId,@RequestBody RescheduleRequest rescheduleRequest, @RequestHeader("Authorization") String bearerToken){
        return rescheduleService.reschedule(transactionId, rescheduleRequest, bearerToken);
    }
}
