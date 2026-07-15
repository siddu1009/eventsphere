package com.eventsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull private Long bookingId;
    @NotBlank private String method;
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
