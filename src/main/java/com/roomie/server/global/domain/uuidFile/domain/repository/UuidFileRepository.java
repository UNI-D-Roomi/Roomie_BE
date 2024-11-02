package com.roomie.server.global.domain.uuidFile.domain.repository;


import com.roomie.server.global.domain.uuidFile.domain.UuidFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidFileRepository extends JpaRepository<UuidFile, Long> {
}
