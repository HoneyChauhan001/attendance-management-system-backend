package com.example.ams.repo;

import com.example.ams.model.CorrectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CorrectionRepo extends JpaRepository<CorrectionRequest, UUID> { }
