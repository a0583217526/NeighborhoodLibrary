package com.library.user_service.controller;

import com.library.user_service.dto.NeighborhoodDto;
import com.library.user_service.service.NeighborhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping
    public ResponseEntity<List<NeighborhoodDto>> getAllNeighborhoods(){
        List<NeighborhoodDto> neighborhoods=neighborhoodService.getAllNeighborhoods();
        return ResponseEntity.ok(neighborhoods);
    }
}
