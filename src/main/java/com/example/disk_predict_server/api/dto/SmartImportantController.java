package com.example.disk_predict_server.api.dto;

import com.example.disk_predict_server.api.dto.response.SmartImportantResponse;
import com.example.disk_predict_server.persistence.smart.SmartImportant;
import com.example.disk_predict_server.service.SmartImportantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/smart")
@Tag(name = "Smart")
@RequiredArgsConstructor
public class SmartImportantController {
    private final SmartImportantService smartImportantService;
//    @Autowired
//    SmartImportantController(SmartImportantService smartImportantService) {
//        this.smartImportantService = smartImportantService;
//    }


    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getListNote(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) throws Exception {
        Map<String, Object> response = new HashMap<>();
        List<SmartImportant> smartImportantList = smartImportantService.getListNote(fromDate, toDate);
        List<SmartImportantResponse> noteResponses = smartImportantList.stream()
                .map(smart -> {
                    SmartImportantResponse smartImportantResponse =
                            SmartImportantResponse.builder()
                                    .id(smart.getId())
                                    .power_on_hours(smart.getPower_on_hours())
                                    .power_cycle(smart.getPower_cycle())
                                    .unsafe_shutdowns(smart.getUnsafe_shutdowns())
                                    .temperature(smart.getTemperature())
                                    .read_error_rate(smart.getRead_error_rate())
                                    .date(smart.getDate())
                                    .class_prediction(smart.getClass_prediction())
                                    .build();
                    // Đặt các trường của notiResponse dựa trên các trường của notification
                    // Ví dụ: notiResponse.setSomeField(notification.getSomeField());
                    return smartImportantResponse;
                })
                .collect(Collectors.toList());
        response.put("data", smartImportantList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> getAllSortPage(
//            @RequestParam(defaultValue = "3") int size,
//            @RequestParam(defaultValue = "date,id") String[] sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(required = false) String date
//            ) throws Exception {
//            try {
//                List<Order> orders = new ArrayList<Order>();
//                if (sort[0].contains(",")) {
//                    // will sort more than 2 fields
//                    // sortOrder="field, direction"
//                    for (String sortOrder : sort) {
//                        String[] _sort = sortOrder.split(",");
//                        orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
//                    }
//                } else {
//                    // sort=[field, direction]
//                    orders.add(new Order(getSortDirection(sort[1]), sort[0]));
//                }
//                List<SmartImportant> smartList = new ArrayList<SmartImportant>();
//                Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
//                Page<SmartImportant> pageTuts;
//                pageTuts = smartImportantService.getAllSortPage(date, pagingSort);
//                smartList = pageTuts.getContent();
//                if (smartList.isEmpty()) {
//                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//                }
//                Map<String, Object> response = new HashMap<>();
//                response.put("smartList", smartList);
//                response.put("currentPage", pageTuts.getNumber());
//                response.put("totalItems", pageTuts.getTotalElements());
//                response.put("totalPages", pageTuts.getTotalPages());
//
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
//            catch (Exception e) {
//                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllSortPage(
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "date,id") String[] sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        try {
            List<Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
            Page<SmartImportant> pageTuts = smartImportantService.getAllSortPage(date, fromDate, toDate, pagingSort);
            List<SmartImportant> smartList = pageTuts.getContent();
//            if (smartList.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }

            Map<String, Object> response = new HashMap<>();
            response.put("smartList", smartList);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Sort.Direction getSortDirection(String direction) {
        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Direction.DESC;
        } else {
            return Sort.Direction.ASC;
        }
    }
}
