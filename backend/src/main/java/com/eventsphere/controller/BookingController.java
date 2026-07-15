package com.eventsphere.controller;
import com.eventsphere.dto.BookingRequest;
import com.eventsphere.dto.BookingDetails;
import com.eventsphere.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService; public BookingController(BookingService bookingService) { this.bookingService=bookingService; }
    @GetMapping public List<BookingDetails> mine(Authentication auth) { return bookingService.getForUser(auth.getName()); }
    @GetMapping("/{id}") public BookingDetails get(Authentication auth, @PathVariable Long id) { return bookingService.getForUser(auth.getName(), id); }
    @PostMapping public ResponseEntity<BookingDetails> create(Authentication auth, @Valid @RequestBody BookingRequest request) { return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(bookingService.create(auth.getName(), request)); }
    @PostMapping("/{id}/cancel") public BookingDetails cancel(Authentication auth, @PathVariable Long id) { return bookingService.cancel(auth.getName(), id); }
}
