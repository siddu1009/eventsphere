package com.eventsphere.controller;

import com.eventsphere.dto.BookingDetails;
import com.eventsphere.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserBookingController {
    private final BookingService bookingService;
    public UserBookingController(BookingService bookingService) { this.bookingService = bookingService; }
    @GetMapping("/bookings")
    public List<BookingDetails> bookings(Authentication auth) { return bookingService.getForUser(auth.getName()); }
}
