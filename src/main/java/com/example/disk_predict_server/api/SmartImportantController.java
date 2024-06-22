package com.example.disk_predict_server.api;

import com.example.disk_predict_server.api.dto.request.SmartInsertRequest;
import com.example.disk_predict_server.api.dto.response.HardDriveOveral;
import com.example.disk_predict_server.api.dto.response.SmartImportantResponse;
import com.example.disk_predict_server.api.dto.response.SmartImportantSortResponse;
import com.example.disk_predict_server.persistence.model.smart.SmartImportant;
import com.example.disk_predict_server.service.SmartImportantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addSmartImportant(@RequestBody SmartInsertRequest smartRequest) {
        SmartInsertRequest a = smartRequest;
        Map<String, String> response = new HashMap<>();
        response = smartImportantService.addSmart(smartRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getListBySerialNum(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
             @RequestParam(required = false) String serialNumber
    ) throws Exception {
        Map<String, Object> response = new HashMap<>();
        List<SmartImportant> smartImportantList = smartImportantService.getListSmart(fromDate, toDate, serialNumber);
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "serial_number,date,id") String[] sort,
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
            List<SmartImportantSortResponse> smartImportantList = smartList.stream()
                    .map(smart -> {
                        String timeLeft;
                        switch(smart.getClass_prediction()) {
                            case 0:
                                timeLeft = "0 - 3 days";
                                break;
                            case 1:
                                timeLeft = "4 - 7 days";
                                break;
                            case 2:
                                timeLeft = "8 - 14 days";
                                break;
                            case 3:
                                timeLeft = "> 14 days";
                                break;
                            default:
                                timeLeft = "> 14 days";
                        }
                        SmartImportantSortResponse smartImportantResponse =
                                SmartImportantSortResponse.builder()
                                        .id(smart.getId())
                                        .power_on_hours(smart.getPower_on_hours())
                                        .power_cycle(smart.getPower_cycle())
                                        .unsafe_shutdowns(smart.getUnsafe_shutdowns())
                                        .temperature(smart.getTemperature())
                                        .read_error_rate(smart.getRead_error_rate())
                                        .date(smart.getDate())
                                        .serial_number(smart.getSerial_number())
                                        .time_left_prediction(timeLeft)
                                        .build();
                        // Đặt các trường của notiResponse dựa trên các trường của notification
                        // Ví dụ: notiResponse.setSomeField(notification.getSomeField());
                        return smartImportantResponse;
                    })
                    .collect(Collectors.toList());
//            if (smartList.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }

            Map<String, Object> response = new HashMap<>();
            response.put("smartList", smartImportantList);
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

    @GetMapping(path = "/serial", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSerialNumber() {
        Map<String, Object> response = new HashMap<>();
        List<String> serialNumbers = smartImportantService.getSerialNumber();
        response.put("serialNumbers", serialNumbers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(path= "/overal", produces = MediaType.APPLICATION_JSON_VALUE)
      public ResponseEntity<Object> getOveral() {
        Map<String, Object> response = new HashMap<>();
        List<SmartImportant> smartImportantList  = smartImportantService.getOveral();
        List<HardDriveOveral> hardDriveOverals = smartImportantList.stream()
                .map(smart -> {
                    String timeLeft;
                    switch(smart.getClass_prediction()) {
                        case 0:
                            timeLeft = "0 - 3 days";
                            break;
                        case 1:
                            timeLeft = "4 - 7 days";
                            break;
                        case 2:
                            timeLeft = "8 - 14 days";
                            break;
                        case 3:
                            timeLeft = "> 14 days";
                            break;
                        default:
                            timeLeft = "> 14 days";
                    }
                    HardDriveOveral hardDriveOveral = HardDriveOveral.builder()
                            .serialNumber(smart.getSerial_number())
                            .date(smart.getDate())
                            .timeLeft(timeLeft)
                            .build();
                    return hardDriveOveral;
                })
                .collect(Collectors.toList());
        response.put("data", hardDriveOverals);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
