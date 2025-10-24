//package com.example.cinema_booking.utils;
//
//import com.example.cinema_booking.dto.request.ShiftRequest;
//import com.example.cinema_booking.dto.request.StaffShiftRequest;
//import com.example.cinema_booking.exception.InsertException;
//import com.example.cinema_booking.model.Account;
//import com.example.cinema_booking.model.Shift;
//import com.example.cinema_booking.repository.StaffShiftRepository;
//import org.springframework.stereotype.Component;
//
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Date;
//
//@Component
//public class ShiftValidationUtil {
//
//    private final StaffShiftRepository staffShiftRepository;
//
//    public ShiftValidationUtil(StaffShiftRepository staffShiftRepository) {
//        this.staffShiftRepository = staffShiftRepository;
//    }
//
//    /**
//     * Validate ShiftRequest khi tạo mới shift
//     */
//    public void validateShiftRequest(ShiftRequest request) {
//        // Validate name
//        if (request.getName() == null || request.getName().trim().isEmpty()) {
//            throw new InsertException("Tên ca làm việc không được để trống");
//        }
//
//        // Validate startTime
//        if (request.getStartTime() == null) {
//            throw new InsertException("Thời gian bắt đầu không được để trống");
//        }
//
//        // Validate endTime
//        if (request.getEndTime() == null) {
//            throw new InsertException("Thời gian kết thúc không được để trống");
//        }
//
//        // Validate startTime < endTime
//        if (request.getStartTime().compareTo(request.getEndTime()) >= 0) {
//            throw new InsertException("Thời gian bắt đầu phải trước thời gian kết thúc");
//        }
//
//        // Validate shift duration (không quá 12 giờ)
//        long durationInMillis = request.getEndTime().getTime() - request.getStartTime().getTime();
//        long hours = durationInMillis / (1000 * 60 * 60);
//        if (hours > 12) {
//            throw new InsertException("Ca làm việc không được quá 12 giờ");
//        }
//
//        // Validate quantity
//        if (request.getQuantity() <= 0) {
//            throw new InsertException("Số lượng nhân viên phải lớn hơn 0");
//        }
//
//        // Validate color
//        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
//            throw new InsertException("Màu sắc không được để trống");
//        }
//    }
//
//    /**
//     * Validate ShiftRequest khi cập nhật shift
//     */
//    public void validateShiftRequestForUpdate(ShiftRequest request, int shiftId) {
//        validateShiftRequest(request);
//
//        // Có thể thêm validation khác cho update nếu cần
//    }
//
//    /**
//     * Validate shiftId
//     */
//    public void validateShiftId(int shiftId) {
//        if (shiftId <= 0) {
//            throw new InsertException("ID ca làm việc không hợp lệ");
//        }
//    }
//
//    /**
//     * Validate staffId
//     */
//    public void validateStaffId(int staffId) {
//        if (staffId <= 0) {
//            throw new InsertException("ID nhân viên không hợp lệ");
//        }
//    }
//
//    /**
//     * Validate shiftDate
//     */
//    public void validateShiftDate(Date shiftDate) {
//        if (shiftDate == null) {
//            throw new InsertException("Ngày làm việc không được để trống");
//        }
//
//        // Convert Date to LocalDate for easier comparison
//        LocalDate shiftLocalDate = shiftDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate today = LocalDate.now();
//        LocalDate threeMonthsLater = today.plusMonths(3);
//
//        // Không cho phép phân ca trong quá khứ
//        if (shiftLocalDate.isBefore(today)) {
//            throw new InsertException("Không thể phân ca trong quá khứ");
//        }
//
//        // Không cho phép phân ca quá xa trong tương lai (> 3 tháng)
//        if (shiftLocalDate.isAfter(threeMonthsLater)) {
//            throw new InsertException("Không thể phân ca quá 3 tháng trong tương lai");
//        }
//    }
//
//    /**
//     * Validate staff account
//     */
//    public void validateStaffAccount(Account staff) {
//        if (staff == null) {
//            throw new InsertException("Nhân viên không tồn tại");
//        }
//
//        // Kiểm tra role (giả sử có field role hoặc status)
//        if (staff.getRole() == null || !staff.getRole().getName().equals("STAFF")) {
//            throw new InsertException("Tài khoản không có quyền nhân viên");
//        }
//
//        // Có thể thêm validation cho trạng thái tài khoản nếu có
//        // if (staff.getStatus() != AccountStatus.ACTIVE) {
//        //     throw new InsertException("Tài khoản không hoạt động");
//        // }
//    }
//
//    /**
//     * Validate shift assignment không bị trùng lịch
//     */
//    public void validateNoScheduleConflict(int staffId, int shiftId, Date shiftDate) {
//        // Kiểm tra xem nhân viên đã được phân ca cho ngày này chưa
//        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(shiftDate);
//        long existingAssignments = staffShiftRepository.countByStaffIdAndShiftDate(staffId, dateStr);
//
//        if (existingAssignments > 0) {
//            throw new InsertException("Nhân viên đã được phân ca cho ngày này");
//        }
//    }
//
//    /**
//     * Validate shift có thể xóa (không có nhân viên nào được phân ca)
//     */
//    public void validateShiftCanBeDeleted(int shiftId) {
//        long staffCount = staffShiftRepository.countByShiftId(shiftId);
//        if (staffCount > 0) {
//            throw new InsertException("Không thể xóa ca làm việc đã có nhân viên được phân công");
//        }
//    }
//
//    /**
//     * Validate date cho các query
//     */
//    public void validateQueryDate(Date date) {
//        if (date == null) {
//            throw new InsertException("Ngày không được để trống");
//        }
//
//        LocalDate queryDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
//
//        if (queryDate.isBefore(oneYearAgo)) {
//            throw new InsertException("Không thể truy vấn dữ liệu quá 1 năm trước");
//        }
//    }
//}