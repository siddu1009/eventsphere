package com.eventsphere.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_user_email", columnList = "user_email"),
        @Index(name = "idx_bookings_event_id", columnList = "event_id")
})
public class BookingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false) private Long eventId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private EventEntity event;
    private String eventTitle;
    @Column(name = "user_email", nullable = false) private String userEmail;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_email", referencedColumnName = "email", insertable = false, updatable = false)
    private UserEntity user;
    @Column(nullable = false) private Integer quantity;
    @Enumerated(EnumType.STRING) private BookingStatus status = BookingStatus.PENDING_PAYMENT;
    @Column(nullable = false, unique = true) private String confirmationCode;
    @Column(nullable = false, unique = true) private String ticketNumber;
    @Column(nullable = false) private Instant createdAt = Instant.now();
    public Long getId() { return id; } public Long getEventId() { return eventId; } public void setEventId(Long eventId) { this.eventId = eventId; }
    public EventEntity getEvent() { return event; }
    public UserEntity getUser() { return user; }
    public String getEventTitle() { return eventTitle; } public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getUserEmail() { return userEmail; } public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BookingStatus getStatus() { return status; } public void setStatus(BookingStatus status) { this.status = status; }
    public String getConfirmationCode() { return confirmationCode; } public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
    public String getTicketNumber() { return ticketNumber; } public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public Instant getCreatedAt() { return createdAt; }
}
