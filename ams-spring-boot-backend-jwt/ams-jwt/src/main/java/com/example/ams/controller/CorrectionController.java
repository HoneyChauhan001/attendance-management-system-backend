package com.example.ams.controller;

import com.example.ams.model.AttendanceEntry;
import com.example.ams.model.CorrectionRequest;
import com.example.ams.model.User;
import com.example.ams.repo.AttendanceRepo;
import com.example.ams.repo.CorrectionRepo;
import com.example.ams.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/attendance/corrections")
@RequiredArgsConstructor
public class CorrectionController {
  private final CorrectionRepo corrections;
  private final AttendanceRepo attendance;
  private final CurrentUserService currentUserService;

  public record CreateReq(UUID attendanceId, String proposedInTime, String proposedOutTime, String reason) {}

  @PostMapping
  public CorrectionRequest create(@RequestBody CreateReq req) {
    User user = currentUserService.currentUser();
    AttendanceEntry a;
    if (req.attendanceId() != null) {
      a = attendance.findById(req.attendanceId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance not found"));
    } else {
      LocalDate today = LocalDate.now(ZoneId.systemDefault());
      a = attendance.openEntry(user.getId(), today).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "no target attendance"));
    }

    // ✅ check if correction is allowed within time window
    if (a.getWorkDate().isBefore(LocalDate.now().minusDays(2))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correction window has expired for this entry");
    }

    CorrectionRequest c = new CorrectionRequest();
    c.setAttendance(a); c.setRequester(user);
    if (req.proposedInTime()!=null) c.setProposedInTime(Instant.parse(req.proposedInTime()));
    if (req.proposedOutTime()!=null) c.setProposedOutTime(Instant.parse(req.proposedOutTime()));
    c.setReason(req.reason()!=null ? req.reason() : "N/A");
    c.setStatus("PENDING");
    c.setExpiresAt(Instant.now().plusSeconds(48*3600));
    return corrections.save(c);
  }

  @GetMapping
  public List<CorrectionRequest> list(@RequestParam(required = false,name = "status") String status,
                                      @RequestParam(required = false, name = "mine") Boolean mine) {
    User user = currentUserService.currentUser();
    List<CorrectionRequest> all = corrections.findAll();
    return all.stream().filter(c -> (status==null || c.getStatus().equals(status)) &&
        (mine==null || !mine || c.getRequester().getId().equals(user.getId()))).toList();
  }

  @GetMapping("/all")
  public List<CorrectionRequest> listAll(@RequestParam(required = false, name = "status") String status) {
    User user = currentUserService.currentUser();
    if (!user.getRole().equals("APPROVER")) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }
    List<CorrectionRequest> all = corrections.findAll();
    return all.stream()
            .filter(c -> status == null || c.getStatus().equalsIgnoreCase(status))
            .toList();
  }

  @PatchMapping("/{id}/decision")
  public CorrectionRequest decide(
          @PathVariable(name = "id") UUID id,
          @RequestBody Map<String,String> body) {
    User user = currentUserService.currentUser();
    if (!user.getRole().equals("APPROVER")) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    CorrectionRequest c = corrections.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // ✅ check if correction request is expired
    if (c.getExpiresAt() != null && Instant.now().isAfter(c.getExpiresAt())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correction request expired");
    }

    String s = body.getOrDefault("status","").toUpperCase();
    if (!s.equals("APPROVED") && !s.equals("REJECTED")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be APPROVED or REJECTED");
    }

    c.setStatus(s);
    c.setDecidedBy(user);
    c.setDecidedAt(Instant.now());
    corrections.save(c);

    // ✅ If approved, update the linked AttendanceEntry
    if (s.equals("APPROVED")) {
      AttendanceEntry entry = attendance.findById(c.getAttendance().getId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance entry not found"));
      if(entry.getStatus().equals("INVALID")){
        entry.setStatus("PRESENT");
      }

      if (c.getProposedInTime() != null) {
        entry.setInTime(c.getProposedInTime());
      }
      if (c.getProposedOutTime() != null) {
        entry.setOutTime(c.getProposedOutTime());
      }
      attendance.save(entry);
    }

    return c;
  }
}
