package com.ha.dxl.room.repository;

import com.ha.dxl.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Room entities.
 * @see US-003
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    Optional<Room> findByName(String name);

    boolean existsByName(String name);
}
