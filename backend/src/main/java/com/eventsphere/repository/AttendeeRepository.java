package com.eventsphere.repository;

import com.eventsphere.entity.AttendeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<AttendeeEntity, Long> {
    Optional<AttendeeEntity> findByBookingId(Long bookingId);
}
