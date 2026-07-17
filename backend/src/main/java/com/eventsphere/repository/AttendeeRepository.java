package com.eventsphere.repository;

import com.eventsphere.entity.AttendeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendeeRepository extends JpaRepository<AttendeeEntity, Long> {
    List<AttendeeEntity> findByBookingIdOrderByIdAsc(Long bookingId);
}
