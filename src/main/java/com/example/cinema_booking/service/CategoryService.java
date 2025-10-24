package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Category;
import com.example.cinema_booking.dto.request.CategoryRequestDTO;
import com.example.cinema_booking.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    Category addCategory(CategoryRequestDTO categoryRequestDTO);

    List<CategoryResponseDTO> getAllCategories();

    Category updateCategory(int categoryId, CategoryRequestDTO categoryRequestDTO);

    void deleteCategory(int categoryId);

    CategoryResponseDTO getCategoryById(int id);
}
