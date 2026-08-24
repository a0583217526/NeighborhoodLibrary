package com.library.user_service.service;

import com.library.user_service.dto.NeighborhoodDto;
import com.library.user_service.repository.NeighborhoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;

    @Transactional
    public List<NeighborhoodDto> getAllNeighborhoods(){
        return neighborhoodRepository.findAllByOrderByNameAsc()
                .stream()
                .map(neighborhood -> new NeighborhoodDto(
                        neighborhood.getId(),
                        neighborhood.getName()
                        )).toList();
    }
}
