package com.example.disk_predict_server.service;

import com.example.disk_predict_server.persistence.smart.SmartImportant;
import com.example.disk_predict_server.persistence.smart.SmartImportantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.time.format.DateTimeFormatter;


@Service
public class SmartImportantService {
    private final SmartImportantRepository smartImportantRepo;
    private final AuthenticationService authenticationService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Autowired
    SmartImportantService(SmartImportantRepository smartImportantRepo, AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.smartImportantRepo = smartImportantRepo;
    }
    public List<SmartImportant> getListNote(String fromDate, String toDate) {
        String userId = authenticationService.getUserId();
        String from = fromDate != null ? LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).atStartOfDay().format(dateFormatter) : null;
        String to = toDate != null ? LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE).atTime(23, 59).format(dateFormatter) : null;
        Map<String, Object> resInfo = new HashMap<>();
        List<SmartImportant> resList;
        resList = smartImportantRepo.getListSerialNumber(userId, from, to);
        return resList;
    }

//    public Page<SmartImportant> getAllSortPage(String date, Pageable pagingSort) {
//        if (date == null)
//            return  smartImportantRepo.findAll(pagingSort);
//        else
//            return smartImportantRepo.findByDateContaining(date, pagingSort);
//    }

    public Page<SmartImportant> getAllSortPage(String date, String fromDate, String toDate, Pageable pageable) {
        String userId = authenticationService.getUserId();
        String from = fromDate != null ? LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).atStartOfDay().format(dateFormatter) : null;
        String to = toDate != null ? LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE).atTime(23, 59).format(dateFormatter) : null;
        Page<SmartImportant> smartImportantList =  smartImportantRepo.findByDateRange(userId, date, from, to, pageable);
        return smartImportantRepo.findByDateRange(userId, date, from, to, pageable);
    }


}
