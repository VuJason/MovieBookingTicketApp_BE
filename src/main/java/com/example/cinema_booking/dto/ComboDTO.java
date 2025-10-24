package com.example.cinema_booking.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboDTO {
    @NotBlank(message = "Tên combo không được để trống")
    private String name;

    @Size(max = 500, message = "Mô tả không quá 500 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private Double price;

    @NotBlank(message = "Danh mục không được để trống")
    private String category;
    private String image;

    private Boolean status = true;
}
