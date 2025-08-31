package com.example.ams.jobs;

import com.example.ams.config.AppProperties;
import com.example.ams.model.AttendanceEntry;
import com.example.ams.repo.AttendanceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoInvalidateJob {
  private final AttendanceRepo repo;
  private final AppProperties props;

//  @Scheduled(cron = "0 0 * * * *") // every hour
  @Scheduled(cron = "0 */2 * * * *") // every 2 minutes (for testing)
  public void run() {
    log.info("cron job is running");
    int count = 0;
    Instant now = Instant.now();
    for (AttendanceEntry e : repo.findByOutTimeIsNull()) {
//      if (Duration.between(e.getInTime(), now).toHours() > props.maxShiftHours()) {
//        e.setStatus("INVALID");
//        repo.save(e);
//        count++;
//      }
      if (Duration.between(e.getInTime(), now).toMinutes() > 5) {
        e.setStatus("INVALID");
        repo.save(e);
        count++;
      }
    }
    if (count > 0) {
      log.info("AutoInvalidateJob: invalidated {} entries", count);
    }
  }
}
