package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.exception.InsertException;
import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.model.Shift;
import com.example.cinema_booking.model.StaffShift;
import com.example.cinema_booking.repository.AccountRepository;
import com.example.cinema_booking.repository.ShiftRepository;
import com.example.cinema_booking.repository.StaffShiftRepository;
//import com.example.cinema_booking.utils.ShiftValidationUtil;
import com.example.cinema_booking.dto.request.ShiftRequest;
import com.example.cinema_booking.dto.request.StaffShiftRequest;
import com.example.cinema_booking.dto.response.AccountResponse;
import com.example.cinema_booking.dto.response.ShiftDetailResponse;
import com.example.cinema_booking.dto.response.ShiftResponse;
import com.example.cinema_booking.dto.response.StaffShiftResponse;
import com.example.cinema_booking.dto.response.ShiftAssignmentCountDTO;
import com.example.cinema_booking.dto.response.StaffInShiftDTO;
import com.example.cinema_booking.service.ShiftService;
import com.example.cinema_booking.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import com.example.cinema_booking.dto.response.BaseResponse;

@Service
public class ShiftServiceImp implements ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StaffShiftRepository staffShiftRepository;

    @Autowired
    private EmailService emailService;



    @Override
    public Shift addShift(ShiftRequest shiftRequest) {
        // Validate input data
//        validationUtil.validateShiftRequest(shiftRequest);
        
        Optional<Shift> existedShift = shiftRepository.findByName(shiftRequest.getName());
        if(existedShift.isPresent()){
            throw new InsertException("Shift already exists");
        }
        else {
            Shift shift = new Shift();
            shift.setName(shiftRequest.getName());
            shift.setStartTime(shiftRequest.getStartTime());
            shift.setEndTime(shiftRequest.getEndTime());
            shift.setRequired_staff(shiftRequest.getQuantity());
            shift.setColor(shiftRequest.getColor());
            shiftRepository.save(shift);
            return shift;
        }

    }

    @Override
    public List<Shift> getAllShifts(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
//        List<Shift> shifts = shiftRepository.findAll(page);
        return shiftRepository.findAll(page).getContent();
    }

    @Override
    public Shift updateShift(int shiftId, ShiftRequest shiftRequest) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(() -> new EntityNotFoundException("Shift not found"));
        
        // Check if new name conflicts with existing shift (excluding current shift)
        Optional<Shift> existingShiftWithName = shiftRepository.findByName(shiftRequest.getName());
        if (existingShiftWithName.isPresent() && existingShiftWithName.get().getId() != shiftId) {
            throw new InsertException("Tên ca làm việc đã tồn tại");
        }
        
        shift.setName(shiftRequest.getName());
        shift.setStartTime(shiftRequest.getStartTime());
        shift.setEndTime(shiftRequest.getEndTime());
        shift.setRequired_staff(shiftRequest.getQuantity());
        shift.setColor(shiftRequest.getColor());
        shiftRepository.save(shift);
        return shift;
    }

    @Override
    public void deleteShift(int shiftId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(() -> new EntityNotFoundException("Shift not found"));
        
        shiftRepository.delete(shift);
    }

    @Override
    public Shift getShift(int shiftId) {
        return shiftRepository.findById(shiftId).orElseThrow(() -> new EntityNotFoundException("Shift not found"));
    }


    @Override
    public StaffShiftResponse assignShift(StaffShiftRequest staffShiftRequest, int staffId, int shiftId) {
        // Validate input data
//        validationUtil.validateStaffId(staffId);
//        validationUtil.validateShiftId(shiftId);
//        validationUtil.validateShiftDate(staffShiftRequest.getShiftDate());
        
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(() -> new EntityNotFoundException("Shift not found"));

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(staffShiftRequest.getShiftDate());
        int currentStaffCount = staffShiftRepository.countByShiftIdAndShiftDate(shiftId, dateStr);
        if (currentStaffCount >= shift.getRequired_staff()) {
            throw new InsertException("Cannot assign more staff to this shift. Maximum staff limit reached.");
        }

        Account staff = accountRepository.findById(staffId).orElseThrow(() -> new EntityNotFoundException("Account not found"));
        
        // Validate staff account
//        validationUtil.validateStaffAccount(staff);
        
        // Validate no schedule conflict
//        validationUtil.validateNoScheduleConflict(staffId, shiftId, staffShiftRequest.getShiftDate());

        StaffShift staffShift = new StaffShift();
        staffShift.setShiftDate(staffShiftRequest.getShiftDate());
        staffShift.setStaff(staff);
        staffShift.setShift(shift);
        staffShiftRepository.save(staffShift);

        // Gửi email thông báo phân ca
        try {
            String subject = "🎬 Thông báo phân ca làm việc";
            String htmlContent = buildShiftAssignmentEmail(staff, shift, staffShiftRequest.getShiftDate());
            emailService.sendHtmlEmail(staff.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến việc phân ca
            System.err.println("Failed to send shift assignment email: " + e.getMessage());
        }

        AccountResponse staffResponse = new AccountResponse();
        staffResponse.setAccountId(staff.getId());
        staffResponse.setEmail(staff.getEmail());
        staffResponse.setAccountName(staff.getFullName());
        staffResponse.setRole(staff.getRole());

        ShiftResponse shiftResponse = new ShiftResponse();
        shiftResponse.setShiftId(shift.getId());
        shiftResponse.setShiftName(shift.getName());
        shiftResponse.setColor(shift.getColor());

        StaffShiftResponse staffShiftResponse = new StaffShiftResponse();
        staffShiftResponse.setShiftDate(staffShift.getShiftDate());
        staffShiftResponse.setAccount(staffResponse);
        staffShiftResponse.setShift(shiftResponse);

        return staffShiftResponse;
    }

    @Override
    public List<ShiftDetailResponse> getShiftsByAccountId() {
        int accountId = (int) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<StaffShift> staffShifts = staffShiftRepository.findByStaffId(accountId);
        
        if (staffShifts.isEmpty()) {
            throw new EntityNotFoundException("Bạn chưa được phân ca làm việc nào");
        }
        
        return staffShifts.stream()
                .map(staffShift -> {
                    ShiftDetailResponse responseDetail = new ShiftDetailResponse();
                    ShiftDetailResponse.Shift shiftInfo = new ShiftDetailResponse.Shift();
                    
                    Shift shift = staffShift.getShift();
                    shiftInfo.setId(shift.getId());
                    shiftInfo.setName(shift.getName());
                    shiftInfo.setStartTime(shift.getStartTime());
                    shiftInfo.setEndTime(shift.getEndTime());
                    shiftInfo.setRequired_staff(shift.getRequired_staff());
                    shiftInfo.setColor(shift.getColor());
                    
                    responseDetail.setShift(shiftInfo);
                    responseDetail.setShiftDate(staffShift.getShiftDate());
                    return responseDetail;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftAssignmentCountDTO> getShiftAssignmentCountByDate(Date date) {
        List<Shift> shifts = shiftRepository.findAll();
        List<ShiftAssignmentCountDTO> result = new ArrayList<>();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        for (Shift shift : shifts) {
            int assigned = staffShiftRepository.countByShiftIdAndShiftDate(shift.getId(), dateStr);
            result.add(new ShiftAssignmentCountDTO(
                shift.getId(),
                shift.getName(),
                assigned,
                shift.getRequired_staff()
            ));
        }
        return result;
    }

    @Override
    public ShiftAssignmentCountDTO getShiftAssignmentCountByDate(int shiftId, Date date) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new EntityNotFoundException("Shift not found"));
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        int assigned = staffShiftRepository.countByShiftIdAndShiftDate(shiftId, dateStr);
        return new ShiftAssignmentCountDTO(
            shift.getId(),
            shift.getName(),
            assigned,
            shift.getRequired_staff()
        );
    }

    @Override
    public List<StaffInShiftDTO> getAccountByShiftId(int shiftId, Date date) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        List<StaffShift> staffShifts = staffShiftRepository.findByShiftIdAndShiftDate(shiftId, dateStr);
        return staffShifts.stream()
                .map(staffShift -> {
                    Account staff = staffShift.getStaff();
                    return new StaffInShiftDTO(
                        staff.getId(),
                        staff.getFullName(),
                        staff.getEmail()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOldShifts() {
        // Lấy ngày đầu tuần hiện tại (Thứ 2)
        LocalDate today = LocalDate.now();
        // Tính ngày thứ 2 của tuần hiện tại
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        
        String dateStr = startOfWeek.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        staffShiftRepository.deleteByShiftDateBefore(dateStr);
        
        System.out.println("Đã xóa lịch ca trước ngày: " + dateStr);
    }
    
    // Tự động xóa lịch ca cũ vào 00:00 mỗi thứ 2 hàng tuần
    @Scheduled(cron = "0 0 0 * * MON")
    public void autoDeleteOldShifts() {
        deleteOldShifts();
    }

    private String buildShiftAssignmentEmail(Account staff, Shift shift, Date shiftDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(shiftDate);
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Thông báo phân ca làm việc</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background-color: #2196f3;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background-color: #f9f9f9;
                        padding: 20px;
                        border-radius: 0 0 5px 5px;
                    }
                    .shift-info {
                        background-color: white;
                        padding: 15px;
                        margin: 15px 0;
                        border-left: 4px solid #2196f3;
                        border-radius: 3px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 20px;
                        color: #666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎬 Thông báo phân ca làm việc</h1>
                </div>
                
                <div class="content">
                    <p>Xin chào <strong>%s</strong>,</p>
                    
                    <p>Bạn đã được phân ca làm việc với thông tin như sau:</p>
                    
                    <div class="shift-info">
                        <h3>📅 Thông tin ca làm việc</h3>
                        <p><strong>Tên ca:</strong> %s</p>
                        <p><strong>Ngày làm việc:</strong> %s</p>
                        <p><strong>Thời gian:</strong> %s - %s</p>
                    </div>
                    
                    <p>Vui lòng đảm bảo có mặt đúng giờ và thực hiện tốt nhiệm vụ được giao.</p>
                    
                    <p>Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ với quản lý.</p>
                    
                    <p>Trân trọng,<br>
                    <strong>Ban quản lý rạp chiếu phim</strong></p>
                </div>
                
                <div class="footer">
                    <p>Email này được gửi tự động từ hệ thống quản lý ca làm việc.</p>
                    <p>Vui lòng không trả lời email này.</p>
                </div>
            </body>
            </html>
            """.formatted(
                staff.getFullName(),
                shift.getName(),
                formattedDate,
                shift.getStartTime(),
                shift.getEndTime()
            );
    }
}
