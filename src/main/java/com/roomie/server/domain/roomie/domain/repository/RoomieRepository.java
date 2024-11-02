package com.roomie.server.domain.roomie.domain.repository;

import com.roomie.server.domain.roomie.domain.Roomie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomieRepository extends JpaRepository<Roomie, Long> {
}
