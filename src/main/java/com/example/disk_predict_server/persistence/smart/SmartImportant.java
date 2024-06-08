package com.example.disk_predict_server.persistence.smart;


import com.example.disk_predict_server.persistence.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartImportant {
    @Id
    @GeneratedValue
    private Integer id;
    private String user_id;
    private Integer power_on_hours;
    private Integer power_cycle;
    private Integer unsafe_shutdowns;
    private Integer temperature;
    private Integer read_error_rate;
    private String serial_number;
    private String date;
    private Integer class_prediction;

}
