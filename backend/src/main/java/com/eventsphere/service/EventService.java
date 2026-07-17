package com.eventsphere.service;

import com.eventsphere.entity.EventEntity;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public EventService(EventRepository eventRepository, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> "PUBLISHED".equals(event.getStatus()))
                .toList();
    }

    public EventEntity create(EventEntity event, String organizerEmail) {
        event.setId(null);
        event.setOrganizerEmail(organizerEmail);
        return eventRepository.save(event);
    }

    public EventEntity getPublicById(Long id) {
        EventEntity event = getExisting(id);
        if (!"PUBLISHED".equals(event.getStatus())) throw new java.util.NoSuchElementException("Event not found");
        return event;
    }

    public EventEntity update(Long id, EventEntity update, String email, boolean admin) {
        EventEntity event = getExisting(id);
        if (!admin && !email.equals(event.getOrganizerEmail())) throw new SecurityException("Only the event organizer can modify this event");
        event.setTitle(update.getTitle()); event.setDescription(update.getDescription()); event.setCategory(update.getCategory());
        event.setLocation(update.getLocation()); event.setOrganizer(update.getOrganizer()); event.setImageUrl(update.getImageUrl());
        event.setPrice(update.getPrice()); event.setSeatsLeft(update.getSeatsLeft()); event.setStartDate(update.getStartDate());
        event.setStartTime(update.getStartTime()); event.setMaxTicketsPerBooking(update.getMaxTicketsPerBooking()); event.setStatus(update.getStatus());
        return eventRepository.save(event);
    }

    public void delete(Long id, String email, boolean admin) {
        EventEntity event = getExisting(id);
        if (!admin && !email.equals(event.getOrganizerEmail())) throw new SecurityException("Only the event organizer can delete this event");
        if (!bookingRepository.findByEventId(id).isEmpty()) throw new IllegalStateException("Events with bookings cannot be deleted");
        eventRepository.delete(event);
    }

    private EventEntity getExisting(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Event not found"));
    }
}
