package com.eventsphere.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "bookings")
public class BookingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long eventId;
    private String eventTitle;
    @Column(nullable = false) private String userEmail;
    @Column(nullable = false) private Integer quantity;
    @Enumerated(EnumType.STRING) private BookingStatus status = BookingStatus.CONFIRMED;
    @Column(nullable = false, unique = true) private String confirmationCode;
    @Column(nullable = false) private Instant createdAt = Instant.now();
    public Long getId() { return id; } public Long getEventId() { return eventId; } public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; } public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getUserEmail() { return userEmail; } public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BookingStatus getStatus() { return status; } public void setStatus(BookingStatus status) { this.status = status; }
    public String getConfirmationCode() { return confirmationCode; } public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
    public Instant getCreatedAt() { return createdAt; }
}
