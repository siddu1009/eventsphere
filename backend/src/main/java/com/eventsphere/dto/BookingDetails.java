package com.eventsphere.dto;

import com.eventsphere.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingDetails(
        Long id,
        Long eventId,
        String eventTitle,
        String eventImageUrl,
        String eventCategory,
        String eventPrice,
        String venue,
        String cityState,
        String organizer,
        LocalDate startDate,
        LocalTime startTime,
        String userEmail,
        Integer quantity,
        BookingStatus status,
        String paymentStatus,
        String confirmationCode,
        String ticketNumber,
        String attendeeName,
        String attendeeEmail,
        BigDecimal ticketAmount,
        BigDecimal convenienceFee,
        BigDecimal gst,
        BigDecimal grandTotal,
        Instant createdAt
) {
    public static BookingDetails from(BookingEntity booking, EventEntity event, AttendeeEntity attendee, PaymentEntity payment) {
        String paymentStatus = payment == null ? "PENDING" : payment.getStatus();
        return new BookingDetails(
                booking.getId(), booking.getEventId(), booking.getEventTitle(),
                event == null ? null : event.getImageUrl(),
                event == null ? null : event.getCategory(),
                event == null ? null : event.getPrice(),
                event == null ? null : event.getLocation(),
                event == null ? null : event.getLocation(),
                event == null ? null : event.getOrganizer(),
                event == null ? null : event.getStartDate(),
                event == null ? null : event.getStartTime(),
                booking.getUserEmail(), booking.getQuantity(), booking.getStatus(),
                paymentStatus, booking.getConfirmationCode(), booking.getTicketNumber(),
                attendee == null ? null : attendee.getFullName(),
                attendee == null ? null : attendee.getEmail(),
                payment == null ? null : payment.getTicketAmount(),
                payment == null ? null : payment.getConvenienceFee(),
                payment == null ? null : payment.getGst(),
                payment == null ? null : payment.getGrandTotal(),
                booking.getCreatedAt()
        );
    }
}
