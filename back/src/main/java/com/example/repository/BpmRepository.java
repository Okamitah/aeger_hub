package com.example.repository;

import com.example.entity.BpmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BpmRepository extends JpaRepository<BpmEntity, Long> {
    List<BpmEntity> findByPatientId(Long patientId);
}