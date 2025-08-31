package com.example.ams.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.util.UUID;

@Entity @Table(name = "attendance_entries")
@Getter @Setter
public class AttendanceEntry {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  private User user;

  @Column(nullable = false)
  private LocalDate workDate;

  @Column(nullable = false)
  private Instant inTime;

  private Double inLat;
  private Double inLng;

  private Instant outTime;
  private Double outLat;
  private Double outLng;

  @Column(nullable = false)
  private String status = "PRESENT"; // PRESENT | INVALID | CORRECTION_PENDING

  private String selfieUrl;
  private String deviceFingerprint;

  @Transient
  public Long getDurationMinutes() {
    return outTime == null ? null : Duration.between(inTime, outTime).toMinutes();
  }
}
