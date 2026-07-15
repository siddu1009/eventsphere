package com.eventsphere.controller;

import com.eventsphere.dto.BookingDetails;
import com.eventsphere.dto.PaymentRequest;
import com.eventsphere.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final BookingService bookingService;
    public PaymentController(BookingService bookingService) { this.bookingService = bookingService; }
    @PostMapping
    public BookingDetails pay(Authentication auth, @Valid @RequestBody PaymentRequest request) {
        return bookingService.pay(auth.getName(), request);
    }
}
