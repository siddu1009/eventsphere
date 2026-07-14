package com.eventsphere.repository;
import com.eventsphere.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BookingRepository extends JpaRepository<BookingEntity, Long> { List<BookingEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail); List<BookingEntity> findByEventId(Long eventId); }
