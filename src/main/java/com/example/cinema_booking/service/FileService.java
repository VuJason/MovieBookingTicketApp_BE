package com.example.cinema_booking.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void copyFile(MultipartFile file);

    Resource loadFile(String filename);
}
