package com.example.cinema_booking.service;


import com.example.cinema_booking.dto.ComboDTO;
import com.example.cinema_booking.model.Combo;

import java.util.List;
import java.util.Optional;

public interface ComboService {

    List<Combo> getAllCombos();

    Optional<Combo> getComboById(Integer id);

    Combo createCombo(ComboDTO dto);

    Combo updateCombo(Integer id, ComboDTO dto);

    void deleteCombo(Integer id);

    List<Combo> searchByName(String name);

    List<Combo> getActiveCombos();
}
