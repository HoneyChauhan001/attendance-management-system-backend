package com.example.ams.repo;

import com.example.ams.model.AttendanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepo extends JpaRepository<AttendanceEntry, UUID> {
  @Query("select a from AttendanceEntry a where a.user.id=:userId and a.workDate=:date")
  List<AttendanceEntry> findByUserAndDate(@Param("userId") UUID userId,
                                          @Param("date") LocalDate date);

  @Query("select a from AttendanceEntry a where a.user.id=:userId and a.workDate=:date and a.outTime is null")
  Optional<AttendanceEntry> openEntry(@Param("userId") UUID userId,
                                      @Param("date") LocalDate date);

  List<AttendanceEntry> findByOutTimeIsNull();
}
