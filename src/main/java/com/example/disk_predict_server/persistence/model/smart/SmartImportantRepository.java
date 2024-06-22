package com.example.disk_predict_server.persistence.model.smart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmartImportantRepository extends JpaRepository<SmartImportant, Integer> {
    @Query("SELECT u FROM SmartImportant u WHERE u.user_id = :user_id AND " +
                    "(COALESCE(:serialNumber, '') = '' OR u.serial_number = :serialNumber) AND " +
                    "(:fromDate IS NULL OR u.date >= :fromDate) AND " +
                    "(:toDate IS NULL OR u.date <= :toDate)")
    List<SmartImportant> getListBySerialNumber(
            String user_id,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("serialNumber") String serialNumber);

    Page<SmartImportant> findByDateContaining(String date, Pageable pageable);

    @Query("SELECT s FROM SmartImportant s WHERE s.user_id = :user_id AND " +
            "(:date IS NULL OR s.date LIKE %:date%) AND " +
            "(:fromDate IS NULL OR s.date >= :fromDate) AND " +
            "(:toDate IS NULL OR s.date <= :toDate)")
    Page<SmartImportant> findByDateRange(
            String user_id,
            @Param("date") String date,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );

    @Query(
            value = "SELECT DISTINCT serial_number FROM smart_important;",
            nativeQuery = true)
    List<String> getSerrialNumber();

    @Query(value = "SELECT s1.* FROM smart_important s1 " +
            "INNER JOIN (SELECT serial_number, MAX(date) AS max_date FROM smart_important WHERE user_id = :user_id GROUP BY serial_number) s2 " +
            "ON s1.serial_number = s2.serial_number AND s1.date = s2.max_date " +
            "WHERE s1.user_id = :user_id", nativeQuery = true)
        List<SmartImportant> getOveral(String user_id );

}
