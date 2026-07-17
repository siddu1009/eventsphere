package com.eventsphere.dto;

import com.eventsphere.entity.AttendeeEntity;

public record AttendeeDetails(Long id, String fullName, String email, String phone, String ticketNumber) {
    public static AttendeeDetails from(AttendeeEntity attendee) {
        return new AttendeeDetails(attendee.getId(), attendee.getFullName(), attendee.getEmail(), attendee.getPhone(), attendee.getTicketNumber());
    }
}
