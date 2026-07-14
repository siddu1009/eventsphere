package com.eventsphere.controller;

import com.eventsphere.entity.EventEntity;
import com.eventsphere.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventEntity>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getPublicById(id));
    }

    @PostMapping
    public ResponseEntity<EventEntity> create(Authentication auth, @Valid @RequestBody EventEntity event) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(eventService.create(event, auth.getName()));
    }

    @PutMapping("/{id}")
    public EventEntity update(Authentication auth, @PathVariable Long id, @Valid @RequestBody EventEntity event) {
        return eventService.update(id, event, auth.getName(), auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        eventService.delete(id, auth.getName(), auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return ResponseEntity.noContent().build();
    }
}
