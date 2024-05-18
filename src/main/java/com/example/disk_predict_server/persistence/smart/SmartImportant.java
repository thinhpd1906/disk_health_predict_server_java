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
    private Integer power_on_hours;
    private Integer power_cycle;
    private Integer unsafe_shutdowns;
    private Integer temperature;
    private Integer read_error_rate;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "serial_number")
//    public User user;
    private String serial_number;
    private Integer class_prediction;
    private String date;
}
