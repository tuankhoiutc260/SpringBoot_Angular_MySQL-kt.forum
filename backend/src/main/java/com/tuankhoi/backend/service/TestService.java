package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.TestRequest;
import com.tuankhoi.backend.dto.response.TestResponse;
import com.tuankhoi.backend.mapper.TestMapper;
import com.tuankhoi.backend.entity.Test;
import com.tuankhoi.backend.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    public TestResponse createTest(TestRequest testRequest) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(testRequest.getTestImage().getBytes());

        Test test = Test.builder()
                .title(testRequest.getTitle())
                .testImage(base64Image)
                .build();

        Test savedTest = testRepository.save(test);
        return TestMapper.INSTANCE.toResponse(savedTest);
    }
}