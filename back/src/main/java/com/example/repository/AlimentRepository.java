package com.example.repository;

import com.example.entity.AlimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlimentRepository extends JpaRepository<AlimentEntity, Long> {
    Optional<AlimentEntity> findByOffId(String offId);
    boolean existsByOffId(String offId);
}
