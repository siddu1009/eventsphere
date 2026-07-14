package com.eventsphere.controller;

import com.eventsphere.dto.UserSummary;
import com.eventsphere.dto.UserUpdateRequest;
import com.eventsphere.entity.Role;
import com.eventsphere.entity.UserEntity;
import com.eventsphere.repository.BookingRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final EventRepository events; private final BookingRepository bookings;
    public AdminController(UserRepository users, EventRepository events, BookingRepository bookings) { this.users=users; this.events=events; this.bookings=bookings; }

    @GetMapping("/users") public List<UserSummary> users() { return users.findAll().stream().map(UserSummary::from).toList(); }

    @PutMapping("/users/{id}") public UserSummary updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest update) {
        UserEntity user = users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (update.getRole() != null) { try { user.setRole(Role.valueOf(update.getRole())); } catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid role"); } }
        if (update.getEnabled() != null) user.setEnabled(update.getEnabled());
        return UserSummary.from(users.save(user));
    }

    @GetMapping("/statistics") public Map<String, Object> statistics() {
        return Map.of("users", users.count(), "organizers", users.findAll().stream().filter(u -> u.getRole() == Role.ORGANIZER).count(),
                "events", events.count(), "bookings", bookings.count(), "availableSeats", events.findAll().stream().map(e -> e.getSeatsLeft() == null ? 0 : e.getSeatsLeft()).mapToInt(Integer::intValue).sum());
    }

    @GetMapping("/reports") public Map<String, Object> reports() {
        return Map.of("events", events.findAll(), "recentBookings", bookings.findAll().stream().sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).limit(20).toList());
    }
}
