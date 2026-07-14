package com.eventsphere.service;
import com.eventsphere.dto.BookingRequest;
import com.eventsphere.entity.*;
import com.eventsphere.repository.BookingRepository;
import com.eventsphere.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository; private final EventRepository eventRepository;
    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository) { this.bookingRepository=bookingRepository; this.eventRepository=eventRepository; }
    @Transactional
    public BookingEntity create(String email, BookingRequest request) {
        EventEntity event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!"PUBLISHED".equals(event.getStatus())) throw new IllegalStateException("This event is not available for booking");
        if (event.getSeatsLeft() == null || event.getSeatsLeft() < request.getQuantity()) throw new IllegalStateException("Not enough seats available");
        event.setSeatsLeft(event.getSeatsLeft() - request.getQuantity());
        BookingEntity booking = new BookingEntity(); booking.setEventId(event.getId()); booking.setEventTitle(event.getTitle()); booking.setUserEmail(email); booking.setQuantity(request.getQuantity()); booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return bookingRepository.save(booking);
    }
    public List<BookingEntity> getForUser(String email) { return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email); }
    @Transactional
    public BookingEntity cancel(String email, Long id) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUserEmail().equals(email)) throw new SecurityException("You cannot cancel this booking");
        if (booking.getStatus() == BookingStatus.CANCELLED) return booking;
        EventEntity event = eventRepository.findById(booking.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.setSeatsLeft((event.getSeatsLeft() == null ? 0 : event.getSeatsLeft()) + booking.getQuantity());
        booking.setStatus(BookingStatus.CANCELLED); return booking;
    }
}
