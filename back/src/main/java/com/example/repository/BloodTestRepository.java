package com.example.repository;

import com.example.entity.BloodTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodTestRepository extends JpaRepository<BloodTestEntity, Long> {
    List<BloodTestEntity> findByPatientId(Long patientId);
}
