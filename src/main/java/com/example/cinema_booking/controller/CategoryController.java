package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.CategoryRequestDTO;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.dto.response.CategoryResponseDTO;
import com.example.cinema_booking.service.imp.CategoryServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryServiceImp categoryServiceImp;


    @PostMapping("/category")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequestDTO category) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(categoryServiceImp.addCategory(category));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryServiceImp.getAllCategories();
        return ResponseEntity.ok(categories);
    }


    @PutMapping("/category/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable int categoryId, @Valid @RequestBody CategoryRequestDTO category) {
        categoryServiceImp.updateCategory(categoryId, category);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Update Successful");
        response.setData(categoryServiceImp.getCategoryById(categoryId));
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable int categoryId) {
        categoryServiceImp.deleteCategory(categoryId);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Delete Successful");
        response.setData("Category with ID = "+categoryId+" has been deleted successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable int categoryId) {
        categoryServiceImp.getCategoryById(categoryId);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(categoryServiceImp.getCategoryById(categoryId));
        return ResponseEntity.ok(response);
    }
}
