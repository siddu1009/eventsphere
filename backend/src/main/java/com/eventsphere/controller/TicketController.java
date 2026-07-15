package com.eventsphere.controller;

import com.eventsphere.dto.BookingDetails;
import com.eventsphere.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final BookingService bookingService;
    public TicketController(BookingService bookingService) { this.bookingService = bookingService; }
    @GetMapping("/{bookingId}")
    public BookingDetails ticket(Authentication auth, @PathVariable Long bookingId) {
        return bookingService.ticket(auth.getName(), bookingId);
    }
}
