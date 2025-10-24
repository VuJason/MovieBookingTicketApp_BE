package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.ComboDTO;
import com.example.cinema_booking.model.Combo;
import com.example.cinema_booking.service.ComboService;
import com.example.cinema_booking.service.imp.ComboServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/combos")
@CrossOrigin(origins = "*")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @GetMapping
    public ResponseEntity<List<Combo>> getAllCombos() {
        return ResponseEntity.ok(comboService.getAllCombos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Combo> getComboById(@PathVariable Integer id) {
        Optional<Combo> combo = comboService.getComboById(id);
        return combo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Combo> createCombo(@Valid @RequestBody ComboDTO dto) {
        return ResponseEntity.status(201).body(comboService.createCombo(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Combo> updateCombo(@PathVariable Integer id, @Valid @RequestBody ComboDTO dto) {
        return ResponseEntity.ok(comboService.updateCombo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCombo(@PathVariable Integer id) {
        comboService.deleteCombo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Combo>> searchCombo(@RequestParam String name) {
        return ResponseEntity.ok(comboService.searchByName(name));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Combo>> getActiveCombos() {
        return ResponseEntity.ok(comboService.getActiveCombos());
    }
}
