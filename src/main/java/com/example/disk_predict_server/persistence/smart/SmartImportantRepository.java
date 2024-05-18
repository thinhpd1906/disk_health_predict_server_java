package com.example.disk_predict_server.persistence.smart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SmartImportantRepository extends JpaRepository<SmartImportant, Integer> {
    @Query("SELECT u FROM SmartImportant u WHERE u.serial_number = :serial_number AND " +
                    "(:fromDate IS NULL OR u.date >= :fromDate) AND " +
                    "(:toDate IS NULL OR u.date <= :toDate)")
    List<SmartImportant> getListSerialNumber(
            String serial_number,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate);

    Page<SmartImportant> findByDateContaining(String date, Pageable pageable);

    @Query("SELECT s FROM SmartImportant s WHERE s.serial_number = :serial_number AND " +
            "(:date IS NULL OR s.date LIKE %:date%) AND " +
            "(:fromDate IS NULL OR s.date >= :fromDate) AND " +
            "(:toDate IS NULL OR s.date <= :toDate)")
    Page<SmartImportant> findByDateRange(
            String serial_number,
            @Param("date") String date,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );
}
