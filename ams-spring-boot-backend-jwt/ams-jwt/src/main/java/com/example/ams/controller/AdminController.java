package com.example.ams.controller;

import com.example.ams.model.AttendanceEntry;
import com.example.ams.model.User;
import com.example.ams.repo.AttendanceRepo;
import com.example.ams.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AttendanceRepo attendance;
  private final CurrentUserService currentUserService;

  @GetMapping("/attendance")
  public List<AttendanceEntry> attendanceByUserAndDate(@RequestParam(name = "employeeId") UUID employeeId,
                                                       @RequestParam(name = "date") String date) {
    User user = currentUserService.currentUser();
    if (!user.getRole().equals("APPROVER")) {
      throw new RuntimeException("Access denied");
    }
    LocalDate d = LocalDate.parse(date);
    return attendance.findByUserAndDate(employeeId, d);
  }

  @GetMapping("/attendance/summary")
  public List<AttendanceEntry> summary(@RequestParam(required = false,name = "employeeId") UUID employeeId,
                                       @RequestParam(name = "from") String from,
                                       @RequestParam(name = "to") String to) {
    User user = currentUserService.currentUser();
    if (!user.getRole().equals("APPROVER")) {
      throw new RuntimeException("Access denied");
    }
    LocalDate f = LocalDate.parse(from);
    LocalDate t = LocalDate.parse(to);
    List<AttendanceEntry> all = attendance.findAll();
    return all.stream().filter(a -> {
      boolean inRange = !a.getWorkDate().isBefore(f) && !a.getWorkDate().isAfter(t);
      boolean empOk = (employeeId == null) || a.getUser().getId().equals(employeeId);
      return inRange && empOk;
    }).toList();
  }

  @PostMapping("/attendance/auto-invalidate")
  public String manualAutoInvalidate() {
    return "Triggered auto-invalidate job (check logs).";
  }
}
