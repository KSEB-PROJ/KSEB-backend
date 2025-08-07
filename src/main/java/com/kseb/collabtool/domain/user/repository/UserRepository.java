package com.kseb.collabtool.domain.user.repository;

import com.kseb.collabtool.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                   "FROM users " +
                   "WHERE created_at >= :startDate " +
                   "GROUP BY DATE(created_at) " +
                   "ORDER BY DATE(created_at)", nativeQuery = true)
    List<Map<String, Object>> findDailyRegistrationsSince(@Param("startDate") LocalDateTime startDate);
}