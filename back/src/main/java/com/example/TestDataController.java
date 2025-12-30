// src/main/java/com/example/TestDataController.java
package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestDataController {

    @Autowired
    private TestDataRepository repository;

    @PostMapping("/save")
    public TestData save(@RequestParam(defaultValue = "Test at ") String msg) {
        String fullMsg = msg + LocalDateTime.now();
        TestData data = new TestData(fullMsg);
        return repository.save(data);
    }

    @GetMapping("/all")
    public List<TestData> getAll() {
        return repository.findAll();
    }
}
