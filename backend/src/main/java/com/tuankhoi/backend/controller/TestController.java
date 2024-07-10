package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.TestRequest;
import com.tuankhoi.backend.dto.response.TestResponse;
import com.tuankhoi.backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/upload")
    public ResponseEntity<TestResponse> uploadTest(@ModelAttribute TestRequest testRequest) throws IOException {
        TestResponse testResponse = testService.createTest(testRequest);
        return ResponseEntity.ok(testResponse);
    }
}