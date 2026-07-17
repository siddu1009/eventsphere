package com.eventsphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attendees", indexes = @Index(name = "idx_attendees_booking_id", columnList = "booking_id"))
public class AttendeeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;
    @Column(nullable = false, unique = true) private String ticketNumber;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String gender;
    @Column(nullable = false) private Integer age;
    @Column(nullable = false) private String address;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String state;
    @Column(nullable = false) private String pinCode;

    public Long getId() { return id; }
    public BookingEntity getBooking() { return booking; }
    public void setBooking(BookingEntity booking) { this.booking = booking; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
}
