package com.example.disk_predict_server.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name="Test")
@RequiredArgsConstructor
public class TestController {
    @GetMapping("")
    public String test() {
        return "test success";
    }
}
