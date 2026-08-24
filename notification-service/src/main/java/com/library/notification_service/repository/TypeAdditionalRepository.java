package com.library.notification_service.repository;

import com.library.notification_service.entity.TypeAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeAdditionalRepository extends JpaRepository<TypeAdditionalDetails, Integer> {
    TypeAdditionalDetails getTypeById(Integer typeId);
    // האינטרפייס הזה נשאר ריק כרגע!
    // הוא יורש מ-JpaRepository ומקבל אוטומטית את כל פונקציות ה-CRUD (כמו findAll) עבור ה-Entity שלנו.
    Optional<TypeAdditionalDetails> findByTypeDetail(String typeDetail);
}


