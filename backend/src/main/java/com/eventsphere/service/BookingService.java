package com.eventsphere.service;
import com.eventsphere.dto.BookingRequest;
import com.eventsphere.dto.BookingDetails;
import com.eventsphere.dto.PaymentRequest;
import com.eventsphere.entity.*;
import com.eventsphere.repository.AttendeeRepository;
import com.eventsphere.repository.BookingRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository; private final EventRepository eventRepository; private final AttendeeRepository attendeeRepository; private final PaymentRepository paymentRepository;
    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository, AttendeeRepository attendeeRepository, PaymentRepository paymentRepository) { this.bookingRepository=bookingRepository; this.eventRepository=eventRepository; this.attendeeRepository=attendeeRepository; this.paymentRepository=paymentRepository; }
    @Transactional
    public BookingDetails create(String email, BookingRequest request) {
        EventEntity event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!"PUBLISHED".equals(event.getStatus())) throw new IllegalStateException("This event is not available for booking");
        if (event.getSeatsLeft() == null || event.getSeatsLeft() < request.getQuantity()) throw new IllegalStateException("Not enough seats available");
        event.setSeatsLeft(event.getSeatsLeft() - request.getQuantity());
        BookingEntity booking = new BookingEntity(); booking.setEventId(event.getId()); booking.setEventTitle(event.getTitle()); booking.setUserEmail(email); booking.setQuantity(request.getQuantity()); booking.setConfirmationCode(code("ESB")); booking.setTicketNumber(code("TKT"));
        BookingEntity saved = bookingRepository.save(booking);
        AttendeeEntity attendee = new AttendeeEntity(); attendee.setBooking(saved); attendee.setFullName(request.getFullName()); attendee.setEmail(request.getEmail()); attendee.setPhone(request.getPhone()); attendee.setGender(request.getGender()); attendee.setAge(request.getAge()); attendee.setAddress(request.getAddress()); attendee.setCity(request.getCity()); attendee.setState(request.getState()); attendee.setPinCode(request.getPinCode());
        attendeeRepository.save(attendee);
        return details(saved, event);
    }
    public List<BookingDetails> getForUser(String email) { return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email).stream().map(this::details).toList(); }
    public BookingDetails getForUser(String email, Long id) { BookingEntity booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found")); ensureOwner(email, booking); return details(booking); }
    @Transactional
    public BookingDetails cancel(String email, Long id) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        ensureOwner(email, booking);
        if (booking.getStatus() == BookingStatus.CANCELLED) return details(booking);
        EventEntity event = eventRepository.findById(booking.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.setSeatsLeft((event.getSeatsLeft() == null ? 0 : event.getSeatsLeft()) + booking.getQuantity());
        booking.setStatus(BookingStatus.CANCELLED); return details(booking, event);
    }
    @Transactional
    public BookingDetails pay(String email, PaymentRequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId()).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        ensureOwner(email, booking);
        if (booking.getStatus() == BookingStatus.CANCELLED) throw new IllegalStateException("Cancelled bookings cannot be paid");
        EventEntity event = eventRepository.findById(booking.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        PaymentEntity payment = paymentRepository.findByBookingId(booking.getId()).orElseGet(PaymentEntity::new);
        payment.setBooking(booking); payment.setMethod(request.getMethod());
        BigDecimal ticketAmount = parsePrice(event.getPrice()).multiply(BigDecimal.valueOf(booking.getQuantity()));
        BigDecimal fee = ticketAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(49);
        BigDecimal gst = fee.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        payment.setTicketAmount(ticketAmount); payment.setConvenienceFee(fee); payment.setGst(gst); payment.setGrandTotal(ticketAmount.add(fee).add(gst)); payment.setStatus("SUCCESS");
        paymentRepository.save(payment); booking.setStatus(BookingStatus.CONFIRMED);
        return details(booking, event);
    }
    public BookingDetails ticket(String email, Long bookingId) { return getForUser(email, bookingId); }
    private BookingDetails details(BookingEntity booking) { return details(booking, eventRepository.findById(booking.getEventId()).orElse(null)); }
    private BookingDetails details(BookingEntity booking, EventEntity event) { return BookingDetails.from(booking, event, attendeeRepository.findByBookingId(booking.getId()).orElse(null), paymentRepository.findByBookingId(booking.getId()).orElse(null)); }
    private void ensureOwner(String email, BookingEntity booking) { if (!booking.getUserEmail().equals(email)) throw new SecurityException("You cannot access this booking"); }
    private String code(String prefix) { return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
    private BigDecimal parsePrice(String price) {
        String digits = price == null ? "" : price.replaceAll("[^0-9.]", "");
        return digits.isBlank() ? BigDecimal.ZERO : new BigDecimal(digits);
    }
}
