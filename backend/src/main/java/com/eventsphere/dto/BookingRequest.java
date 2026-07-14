package com.eventsphere.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class BookingRequest { @NotNull private Long eventId; @NotNull @Min(1) private Integer quantity; public Long getEventId(){return eventId;} public void setEventId(Long eventId){this.eventId=eventId;} public Integer getQuantity(){return quantity;} public void setQuantity(Integer quantity){this.quantity=quantity;} }
