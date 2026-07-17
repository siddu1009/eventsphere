package com.eventsphere.config;

import com.eventsphere.entity.EventEntity;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.CategoryRepository;
import com.eventsphere.entity.CategoryEntity;
import com.eventsphere.entity.UserEntity;
import com.eventsphere.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;

    public DataSeeder(EventRepository eventRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
    }

    @Bean
    public CommandLineRunner seedEvents() {
        return args -> {
            for (String name : java.util.List.of("Music", "Technology", "Business", "Education", "Gaming", "Sports")) seedCategory(name);
            seedUser("Demo Organizer", "organizer@eventsphere.com", Role.ORGANIZER);
            seedUser("Demo Admin", "admin@eventsphere.com", Role.ADMIN);
            if (eventRepository.count() == 0) {
                EventEntity first = new EventEntity();
                first.setTitle("Bengaluru AI & Innovation Summit");
                first.setDescription("A practical summit for India's builders, students, and founders.");
                first.setCategory("Technology");
                first.setLocation("Bengaluru, Karnataka");
                first.setOrganizer("Northstar India");
                first.setOrganizerEmail("organizer@eventsphere.com");
                first.setImageUrl("https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800&q=80");
                first.setPrice("₹1,499");
                first.setSeatsLeft(24);
                first.setStartDate(LocalDate.of(2026, 8, 22));
                first.setStartTime(LocalTime.of(9, 0));
                eventRepository.save(first);

                EventEntity second = new EventEntity();
                second.setTitle("Mumbai Startup Growth Meetup");
                second.setDescription("Hands-on networking for early-stage founders and creators.");
                second.setCategory("Business");
                second.setLocation("Mumbai, Maharashtra");
                second.setOrganizer("Founders Circle India");
                second.setOrganizerEmail("organizer@eventsphere.local");
                second.setImageUrl("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80");
                second.setPrice("Free");
                second.setSeatsLeft(110);
                second.setStartDate(LocalDate.of(2026, 9, 5));
                second.setStartTime(LocalTime.of(18, 30));
                eventRepository.save(second);
            }
        };
    }

    private void seedUser(String name, String email, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            UserEntity user = new UserEntity(); user.setName(name); user.setEmail(email); user.setRole(role);
            user.setPassword(passwordEncoder.encode("Demo1234!")); userRepository.save(user);
        }
    }

    private void seedCategory(String name) {
        if (categoryRepository.findByNameIgnoreCase(name).isEmpty()) { CategoryEntity category = new CategoryEntity(); category.setName(name); categoryRepository.save(category); }
    }
}
