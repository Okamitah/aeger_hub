package com.example.controller;

import com.example.entity.AlimentEntity;
import com.example.service.AlimentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aliments")
public class AlimentController {

    private final AlimentService service;

    public AlimentController(AlimentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlimentEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/search")
    public List<AlimentEntity> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {
        return service.searchPreview(q, pageSize);
    }

    @GetMapping("/category")
    public List<AlimentEntity> byCategory(
            @RequestParam String tag,
            @RequestParam(defaultValue = "20") int pageSize) throws Exception {
        return service.categoryPreview(tag, pageSize);
    }

    @PostMapping("/import")
    public AlimentEntity importOne(@RequestBody AlimentEntity aliment) {
        return service.importOne(aliment);
    }

    @PostMapping("/import/bulk")
    public List<AlimentEntity> importBulk(@RequestBody List<AlimentEntity> aliments) {
        return service.importMany(aliments);
    }
}
