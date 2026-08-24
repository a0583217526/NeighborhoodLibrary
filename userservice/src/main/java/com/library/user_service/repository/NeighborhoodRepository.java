package com.library.user_service.repository;

import com.library.user_service.entity.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {

    Optional<Neighborhood> findByName(String name);

    List<Neighborhood> findAllByOrderByNameAsc();
}
