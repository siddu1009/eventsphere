package com.eventsphere;

import com.eventsphere.config.JwtAuthenticationFilter;
import com.eventsphere.config.SecurityConfig;
import com.eventsphere.dto.BookingDetails;
import com.eventsphere.entity.BookingStatus;
import com.eventsphere.entity.Role;
import com.eventsphere.entity.UserEntity;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.service.BookingService;
import com.eventsphere.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.eventsphere.controller.BookingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class})
class BookingControllerSecurityTest {
    private static final String USER_EMAIL = "new.user@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TestBookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void resetBookingService() {
        bookingService.createCalls = 0;
        bookingService.createEmail = null;
    }

    @Test
    void authenticatedUserCanCreateBooking() throws Exception {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user(Role.USER)));

        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(Role.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingRequest()))
                .andExpect(status().isCreated());

        assertEquals(1, bookingService.createCalls);
        assertEquals(USER_EMAIL, bookingService.createEmail);
    }

    @Test
    void guestCannotCreateBooking() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingRequest()))
                .andExpect(status().isForbidden());

        assertEquals(0, bookingService.createCalls);
    }

    @Test
    void bookingPreflightIsAllowed() throws Exception {
        mockMvc.perform(options("/api/bookings")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk());
    }

    @Test
    void organizerCannotCreateBooking() throws Exception {
        String organizerEmail = "organizer@example.com";
        when(userRepository.findByEmail(organizerEmail)).thenReturn(Optional.of(user(organizerEmail, Role.ORGANIZER)));

        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateToken(organizerEmail, Role.ORGANIZER.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingRequest()))
                .andExpect(status().isForbidden());

        assertEquals(0, bookingService.createCalls);
    }

    @Test
    void adminCannotCreateBooking() throws Exception {
        String adminEmail = "admin@example.com";
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(user(adminEmail, Role.ADMIN)));

        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateToken(adminEmail, Role.ADMIN.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingRequest()))
                .andExpect(status().isForbidden());

        assertEquals(0, bookingService.createCalls);
    }

    @Test
    void organizerEndpointStillRequiresOrganizerRole() throws Exception {
        String organizerEmail = "organizer@example.com";
        when(userRepository.findByEmail(organizerEmail)).thenReturn(Optional.of(user(organizerEmail, Role.ORGANIZER)));

        mockMvc.perform(get("/api/organizer/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateToken(organizerEmail, Role.ORGANIZER.name())))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminEndpointStillRequiresAdminRole() throws Exception {
        String adminEmail = "admin@example.com";
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(user(adminEmail, Role.ADMIN)));

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateToken(adminEmail, Role.ADMIN.name())))
                .andExpect(status().isNotFound());
    }

    private String bearerToken(Role role) {
        return "Bearer " + jwtUtil.generateToken(USER_EMAIL, role.name());
    }

    private UserEntity user(Role role) {
        return user(USER_EMAIL, role);
    }

    private UserEntity user(String email, Role role) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private String validBookingRequest() {
        return """
                {
                  "eventId": 10,
                  "quantity": 1,
                  "fullName": "Demo User",
                  "email": "new.example.com",
                  "phone": "9876543210",
                  "gender": "Other",
                  "age": 28,
                  "address": "MG Road",
                  "city": "Bengaluru",
                  "state": "Karnataka",
                  "pinCode": "560001"
                }
                """;
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        TestBookingService bookingService() {
            return new TestBookingService();
        }
    }

    static class TestBookingService extends BookingService {
        int createCalls;
        String createEmail;

        TestBookingService() {
            super(null, null, null, null);
        }

        @Override
        public BookingDetails create(String email, com.eventsphere.dto.BookingRequest request) {
            createCalls++;
            createEmail = email;
            return new BookingDetails(
                    1L, 10L, "Summit", null, "Technology", "Free", "Bengaluru", "Bengaluru",
                    "Northstar India", null, null, email, 1, BookingStatus.PENDING_PAYMENT,
                    "PENDING", "ESB-12345678", "TKT-12345678", "Demo User", email,
                    List.of(), null, null, null, null, Instant.now());
        }
    }
}
