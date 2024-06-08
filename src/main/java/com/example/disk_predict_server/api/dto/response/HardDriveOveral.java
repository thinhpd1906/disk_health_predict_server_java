package com.example.disk_predict_server.api.dto.response;


import lombok.Builder;

@Builder
public class HardDriveOveral {
    private String serialNumber;
    private String date;
//    private Integer classPrediction;
    private String timeLeft;
    public HardDriveOveral(String serialNumber, String date, String timeLeft) {
        this.serialNumber = serialNumber;
        this.date = date;
//        this.classPrediction = classPrediction;
        this.timeLeft = timeLeft;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDate() {
        return date;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }
}
