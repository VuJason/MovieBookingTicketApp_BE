package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.model.Category;
import com.example.cinema_booking.model.Movie;
import com.example.cinema_booking.repository.CategoryRepository;
import com.example.cinema_booking.repository.MovieRepository;
import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.request.MovieCategoryRequest;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.service.MovieService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Set<String> importedFileNames = new HashSet<>();

    // Lấy tất cả phim
    public List<MovieResponseDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy phim theo ID
    public MovieResponseDTO getMovieById(int id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return convertToResponseDTO(movie);
    }

    // Tạo phim mới
    public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {
        Optional<Movie> existingMovie = movieRepository.findByName(movieRequestDTO.getName());
        if (existingMovie.isPresent()) {
            throw new RuntimeException("Movie with name '" + movieRequestDTO.getName() + "' already exists");
        }

        Movie movie = convertToEntity(movieRequestDTO);
        movie.setCreatedAt(LocalDateTime.now());
        movie.setUpdatedAt(LocalDateTime.now());

        // Gán thể loại từ categoryIds
        if (movieRequestDTO.getCategoryIds() != null && !movieRequestDTO.getCategoryIds().isEmpty()) {
            List<Category> categories = movieRequestDTO.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + id)))
                    .collect(Collectors.toList());
            movie.setCategories(categories);
        }

        Movie savedMovie = movieRepository.save(movie);
        return convertToResponseDTO(savedMovie);
    }

    // Cập nhật phim
    public MovieResponseDTO updateMovie(int id, MovieRequestDTO movieRequestDTO) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        updateMovieFields(movie, movieRequestDTO);
        movie.setUpdatedAt(LocalDateTime.now());

        // Cập nhật category nếu có
        if (movieRequestDTO.getCategoryIds() != null) {
            List<Category> categories = movieRequestDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId)))
                    .collect(Collectors.toList());
            movie.setCategories(categories);
        }

        Movie updatedMovie = movieRepository.save(movie);
        return convertToResponseDTO(updatedMovie);
    }

    public void deleteMovie(int id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    public List<MovieResponseDTO> searchMoviesByName(String name) {
        return movieRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }



    public List<MovieResponseDTO> getComingSoonMovies() {
        return movieRepository.findByIsComingSoon(true)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MovieResponseDTO> getNowShowingMovies() {
        return movieRepository.findByIsComingSoon(false)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieResponseDTO addMovieCategory(MovieCategoryRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        List<Category> categories = request.getCategoryIds().stream()
                .map(categoryId ->
                        categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId)))
                .collect(Collectors.toList());
        movie.setCategories(categories);

        Movie savedMovie = movieRepository.save(movie);
        return convertToResponseDTO(movie);
    }

    // ========== HELPER METHODS ==========

    private MovieResponseDTO convertToResponseDTO(Movie movie) {
        MovieResponseDTO responseDTO = new MovieResponseDTO();
        responseDTO.setId(movie.getId());
        responseDTO.setName(movie.getName());
        responseDTO.setDescription(movie.getDescription());
        responseDTO.setDuration(movie.getDuration());
        responseDTO.setDirector(movie.getDirector());
        responseDTO.setActor(movie.getActor());
        responseDTO.setReleaseDate(movie.getReleaseDate());
        responseDTO.setTrailer(movie.getTrailer());
        responseDTO.setPosterUrl(movie.getPosterUrl());
        responseDTO.setIsComingSoon(movie.getIsComingSoon());
        responseDTO.setEndDate(movie.getEndDate());
        responseDTO.setCreatedAt(movie.getCreatedAt());
        responseDTO.setUpdatedAt(movie.getUpdatedAt());
        responseDTO.setCategoryNames(
                movie.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet())
        );
        return responseDTO;
    }

    private Movie convertToEntity(MovieRequestDTO requestDTO) {
        Movie movie = new Movie();
        movie.setName(requestDTO.getName());
        movie.setDescription(requestDTO.getDescription());
        movie.setDuration(requestDTO.getDuration());
        movie.setDirector(requestDTO.getDirector());
        movie.setActor(requestDTO.getActor());
        movie.setReleaseDate(requestDTO.getReleaseDate());
        movie.setTrailer(requestDTO.getTrailer());
        movie.setPosterUrl(requestDTO.getPosterUrl());
        movie.setIsComingSoon(requestDTO.getIsComingSoon());
        movie.setEndDate(requestDTO.getEndDate());
        return movie;
    }

    private void updateMovieFields(Movie movie, MovieRequestDTO requestDTO) {
        movie.setName(requestDTO.getName());
        movie.setDescription(requestDTO.getDescription());
        movie.setDuration(requestDTO.getDuration());
        movie.setDirector(requestDTO.getDirector());
        movie.setActor(requestDTO.getActor());
        movie.setReleaseDate(requestDTO.getReleaseDate());
        movie.setTrailer(requestDTO.getTrailer());
        movie.setPosterUrl(requestDTO.getPosterUrl());
        movie.setIsComingSoon(requestDTO.getIsComingSoon());
        movie.setEndDate(requestDTO.getEndDate());
    }

    public void importMoviesFromExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (importedFileNames.contains(fileName)) {
            throw new RuntimeException("File đã được import trước đó: " + fileName);
        }
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new RuntimeException("File không có header");
            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().replaceAll("\\s", "").toLowerCase();
                colIndex.put(header, cell.getColumnIndex());
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Movie movie = new Movie();
                movie.setName(getCellString(row.getCell(colIndex.getOrDefault("name", -1))));
                movie.setDescription(getCellString(row.getCell(colIndex.getOrDefault("description", -1))));
                movie.setDuration((int) getCellNumeric(row.getCell(colIndex.getOrDefault("duration", -1))));
                movie.setDirector(getCellString(row.getCell(colIndex.getOrDefault("director", -1))));
                movie.setActor(getCellString(row.getCell(colIndex.getOrDefault("actor", -1))));
                Cell releaseDateCell = row.getCell(colIndex.getOrDefault("releasedate", -1));
                if (releaseDateCell != null) {
                    if (releaseDateCell.getCellType() == CellType.NUMERIC) {
                        movie.setReleaseDate(releaseDateCell.getLocalDateTimeCellValue().toLocalDate());
                    } else {
                        movie.setReleaseDate(java.time.LocalDate.parse(getCellString(releaseDateCell), formatter));
                    }
                }
                Cell endDateCell = row.getCell(colIndex.getOrDefault("enddate", -1));
                if (endDateCell != null) {
                    if (endDateCell.getCellType() == CellType.NUMERIC) {
                        movie.setEndDate(endDateCell.getLocalDateTimeCellValue().toLocalDate());
                    } else {
                        movie.setEndDate(java.time.LocalDate.parse(getCellString(endDateCell), formatter));
                    }
                }
                movie.setTrailer(getCellString(row.getCell(colIndex.getOrDefault("trailer", -1))));
                movie.setPosterUrl(getCellString(row.getCell(colIndex.getOrDefault("posterurl", -1))));
                movieRepository.save(movie);
            }
            importedFileNames.add(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Import movies failed: " + e.getMessage());
        }
    }

    private String getCellString(Cell cell) {
        return cell == null ? null : cell.getStringCellValue();
    }

    private double getCellNumeric(Cell cell) {
        return cell == null ? 0 : cell.getNumericCellValue();
    }
}
