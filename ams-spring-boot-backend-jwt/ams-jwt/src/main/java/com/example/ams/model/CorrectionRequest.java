package com.example.ams.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "correction_requests")
@Getter @Setter
public class CorrectionRequest {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  private AttendanceEntry attendance;

  @ManyToOne(optional = false)
  private User requester;

  private Instant proposedInTime;
  private Instant proposedOutTime;

  @Column(nullable = false)
  private String reason;

  @Column(nullable = false)
  private String status = "PENDING"; // PENDING | APPROVED | REJECTED

  @Column(nullable = false)
  private Instant expiresAt;

  @ManyToOne
  private User decidedBy;

  private Instant decidedAt;
}
