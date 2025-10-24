package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Shift;
import com.example.cinema_booking.dto.request.ShiftRequest;
import com.example.cinema_booking.dto.request.StaffShiftRequest;
import com.example.cinema_booking.dto.response.ShiftDetailResponse;
import com.example.cinema_booking.dto.response.StaffShiftResponse;
import com.example.cinema_booking.dto.response.ShiftAssignmentCountDTO;
import com.example.cinema_booking.dto.response.StaffInShiftDTO;
import com.example.cinema_booking.dto.response.BaseResponse;
import java.util.Date;

import java.util.List;

public interface ShiftService {

    Shift addShift(ShiftRequest shiftRequest);

    List<Shift> getAllShifts(int pageNumber, int pageSize);

    Shift updateShift(int shiftId, ShiftRequest shiftRequest);

    void deleteShift(int shiftId);

    Shift getShift(int shiftId);

    StaffShiftResponse assignShift(StaffShiftRequest staffShiftRequest, int staffId, int shiftId);

    List<ShiftDetailResponse> getShiftsByAccountId();

    List<ShiftAssignmentCountDTO> getShiftAssignmentCountByDate(Date date);

    ShiftAssignmentCountDTO getShiftAssignmentCountByDate(int shiftId, Date date);

    List<StaffInShiftDTO> getAccountByShiftId(int shiftId, Date date);

    void deleteOldShifts();

}
