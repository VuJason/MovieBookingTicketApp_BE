package com.example.cinema_booking.repository;


import com.example.cinema_booking.model.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, Integer> {
    List<StaffShift> findByStaffId(int staffId);
//    int countByShiftIdAndShiftDate(int shiftId, Date shiftDate);

     @Query(value = "SELECT COUNT(*) FROM staff_shift WHERE shift_id = :shiftId AND DATE(shift_date) = :shiftDate", nativeQuery = true)
   int countByShiftIdAndShiftDate(@Param("shiftId") int shiftId, @Param("shiftDate") String shiftDate);
    List<StaffShift> findByStaffIdAndShiftDate(int staffId, Date shiftDate);

    @Query(value = "SELECT * FROM staff_shift WHERE shift_id = :shiftId AND DATE(shift_date) = :shiftDate", nativeQuery = true)
    List<StaffShift> findByShiftIdAndShiftDate(@Param("shiftId") int shiftId, @Param("shiftDate") String shiftDate);
    
    @Query(value = "DELETE FROM staff_shift WHERE DATE(shift_date) < :date", nativeQuery = true)
    void deleteByShiftDateBefore(@Param("date") String date);
    
    @Query(value = "SELECT COUNT(*) FROM staff_shift WHERE staff_id = :staffId AND DATE(shift_date) = :shiftDate", nativeQuery = true)
    long countByStaffIdAndShiftDate(@Param("staffId") int staffId, @Param("shiftDate") String shiftDate);
    
    @Query(value = "SELECT COUNT(*) FROM staff_shift WHERE shift_id = :shiftId", nativeQuery = true)
    long countByShiftId(@Param("shiftId") int shiftId);
}
