package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.model.Category;
import com.example.cinema_booking.repository.CategoryRepository;
import com.example.cinema_booking.dto.request.CategoryRequestDTO;
import com.example.cinema_booking.dto.response.CategoryResponseDTO;
import com.example.cinema_booking.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    @Transactional
    public Category addCategory(CategoryRequestDTO request) {
        Category category = new Category();
        category.setName(request.getCategory());


        return categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDTO getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return convertToResponseDTO(category);
    }

    @Override
    @Transactional
    public Category updateCategory(int categoryId, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

//        Movie movie = movieRepository.findById(request.getMovieId())
//                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

//        category.setMovie(movie);
        category.setName(request.getCategory());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }


    private CategoryResponseDTO convertToResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setCategory(category.getName());
        return dto;
    }

}