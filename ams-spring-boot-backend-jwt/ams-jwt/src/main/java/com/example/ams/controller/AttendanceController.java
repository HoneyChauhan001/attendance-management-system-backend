package com.example.ams.controller;

import com.example.ams.config.AppProperties;
import com.example.ams.model.AttendanceEntry;
import com.example.ams.model.User;
import com.example.ams.repo.AttendanceRepo;
import com.example.ams.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
  private final AttendanceRepo attendance;
  private final AppProperties props;
  private final CurrentUserService currentUserService;

  @PostMapping(value="/clock-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public AttendanceEntry clockIn(@RequestParam(required = false, name = "lat") Double lat,
                                 @RequestParam(required = false, name = "lng") Double lng,
                                 @RequestPart(required = false, name = "selfie") MultipartFile selfie) throws IOException {
    User user = currentUserService.currentUser();
    Instant now = Instant.now();
    LocalDate workDate = LocalDate.now(ZoneId.systemDefault());
    if (attendance.openEntry(user.getId(), workDate).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Open entry exists");
    }
    String selfieUrl = saveSelfie(selfie);
    AttendanceEntry e = new AttendanceEntry();
    e.setUser(user); e.setWorkDate(workDate); e.setInTime(now); e.setInLat(lat); e.setInLng(lng); e.setSelfieUrl(selfieUrl);
    return attendance.save(e);
  }

  @PostMapping(value="/clock-out", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public AttendanceEntry clockOut(@RequestParam(required = false, name = "lat") Double lat,
                                  @RequestParam(required = false, name = "lng") Double lng,
                                  @RequestPart(required = false, name = "selfie") MultipartFile selfie) throws IOException {
    User user = currentUserService.currentUser();
    Instant now = Instant.now();
    LocalDate workDate = LocalDate.now(ZoneId.systemDefault());
    AttendanceEntry e = attendance.openEntry(user.getId(), workDate)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open entry"));
    String selfieUrl = e.getSelfieUrl();
    String newSelfie = saveSelfie(selfie);
    if (newSelfie != null) selfieUrl = newSelfie;
    e.setOutTime(now); e.setOutLat(lat); e.setOutLng(lng); e.setSelfieUrl(selfieUrl);
    return attendance.save(e);
  }

  @GetMapping("/me")
  public List<AttendanceEntry> myAttendance(@RequestParam(name = "date") String date) {
    User user = currentUserService.currentUser();
    LocalDate d = LocalDate.parse(date);
    return attendance.findByUserAndDate(user.getId(), d);
  }

  private String saveSelfie(MultipartFile selfie) throws IOException {
    if (selfie == null || selfie.isEmpty()) return null;
    Path dir = Path.of(props.uploadDir());
    Files.createDirectories(dir);
    Path path = dir.resolve("selfie_" + UUID.randomUUID() + ".jpg");
    selfie.transferTo(path);
    return "/" + path.toString();
  }
}
