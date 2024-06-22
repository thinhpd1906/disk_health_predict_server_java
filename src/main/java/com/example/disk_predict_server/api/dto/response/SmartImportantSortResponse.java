package com.example.disk_predict_server.api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartImportantSortResponse {
    public Integer id;
    public String serial_number;
    public Integer power_on_hours;
    public Integer power_cycle;
    public Integer unsafe_shutdowns;
    public Integer temperature;
    public Integer read_error_rate;
//    public Integer class_prediction;
    public String date;
    public String time_left_prediction;
}
