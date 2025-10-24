package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.dto.ComboDTO;
import com.example.cinema_booking.model.Combo;
import com.example.cinema_booking.repository.ComboRepository;
import com.example.cinema_booking.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComboServiceImp implements ComboService {

    @Autowired
    private ComboRepository comboRepository;

    public List<Combo> getAllCombos() {
        return comboRepository.findAll();
    }

    public Optional<Combo> getComboById(Integer id) {
        return comboRepository.findById(id);
    }

    public Combo createCombo(ComboDTO dto) {
        Combo combo = Combo.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .image(dto.getImage())
                .build();
        return comboRepository.save(combo);
    }

    public Combo updateCombo(Integer id, ComboDTO dto) {
        Combo combo = comboRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy combo"));
        combo.setName(dto.getName());
        combo.setDescription(dto.getDescription());
        combo.setPrice(dto.getPrice());
        combo.setCategory(dto.getCategory());
        combo.setStatus(dto.getStatus());
        combo.setImage(dto.getImage());
        return comboRepository.save(combo);
    }

    public void deleteCombo(Integer id) {
        comboRepository.deleteById(id);
    }

    public List<Combo> searchByName(String name) {
        return comboRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Combo> getActiveCombos() {
        return comboRepository.findByStatus(true);
    }
}