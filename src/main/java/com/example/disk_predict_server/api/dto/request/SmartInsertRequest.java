package com.example.disk_predict_server.api.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmartInsertRequest {
    private int power_on_hours;
    private int power_cycle;
    private int unsafe_shutdowns;
    private int temperature;
    private int read_error_rate;
    private String serial_number;
    private String date;
    private int class_prediction;
}
