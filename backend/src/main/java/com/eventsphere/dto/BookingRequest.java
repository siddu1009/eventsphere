package com.eventsphere.dto;
import jakarta.validation.constraints.*;
public class BookingRequest {
    @NotNull private Long eventId;
    @NotNull @Min(1) private Integer quantity;
    @NotBlank private String fullName;
    @NotBlank @Email private String email;
    @NotBlank @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits") private String phone;
    @NotBlank private String gender;
    @NotNull @Min(1) private Integer age;
    @NotBlank private String address;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank private String pinCode;
    public Long getEventId(){return eventId;} public void setEventId(Long eventId){this.eventId=eventId;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer quantity){this.quantity=quantity;}
    public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; } public void setAge(Integer age) { this.age = age; }
    public String getAddress() { return address; } public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public String getState() { return state; } public void setState(String state) { this.state = state; }
    public String getPinCode() { return pinCode; } public void setPinCode(String pinCode) { this.pinCode = pinCode; }
}
