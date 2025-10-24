package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceImp implements FileService {

    @Override
    public void copyFile(MultipartFile file) {

    }

    @Override
    public Resource loadFile(String filename) {
        return null;
    }
}
