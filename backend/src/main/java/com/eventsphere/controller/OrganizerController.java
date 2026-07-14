package com.eventsphere.controller;

import com.eventsphere.dto.UserSummary;
import com.eventsphere.entity.BookingEntity;
import com.eventsphere.entity.EventEntity;
import com.eventsphere.repository.BookingRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {
    private final EventRepository events;
    private final BookingRepository bookings;
    private final UserRepository users;

    public OrganizerController(EventRepository events, BookingRepository bookings, UserRepository users) {
        this.events = events; this.bookings = bookings; this.users = users;
    }

    @GetMapping("/events")
    public List<EventEntity> myEvents(Authentication auth) {
        return events.findByOrganizerEmail(auth.getName());
    }

    @GetMapping("/events/{id}/attendees")
    public List<Map<String, Object>> attendees(Authentication auth, @PathVariable Long id) {
        EventEntity event = ownedEvent(auth, id);
        return bookings.findByEventId(id).stream().map(booking -> {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("booking", booking);
            result.put("attendee", users.findByEmail(booking.getUserEmail()).map(UserSummary::from).orElse(null));
            result.put("eventTitle", event.getTitle());
            return result;
        }).toList();
    }

    @GetMapping("/analytics")
    public Map<String, Object> analytics(Authentication auth) {
        List<EventEntity> myEvents = events.findByOrganizerEmail(auth.getName());
        int registrations = myEvents.stream().flatMap(event -> bookings.findByEventId(event.getId()).stream())
                .filter(booking -> booking.getStatus().name().equals("CONFIRMED")).mapToInt(BookingEntity::getQuantity).sum();
        return Map.of("events", myEvents.size(), "published", myEvents.stream().filter(e -> "PUBLISHED".equals(e.getStatus())).count(),
                "registrations", registrations, "remainingSeats", myEvents.stream().map(EventEntity::getSeatsLeft).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).sum());
    }

    private EventEntity ownedEvent(Authentication auth, Long id) {
        EventEntity event = events.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!auth.getName().equals(event.getOrganizerEmail())) throw new SecurityException("You cannot view attendees for this event");
        return event;
    }
}
