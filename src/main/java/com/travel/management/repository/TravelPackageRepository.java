package com.travel.management.repository;

import com.travel.management.entity.TravelPackage;
import com.travel.management.enums.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    Page<TravelPackage> findByDestinationId(Long destinationId, Pageable pageable);

    Page<TravelPackage> findByStatus(PackageStatus status, Pageable pageable);

    Page<TravelPackage> findByStatusAndAvailableSeatsGreaterThan(PackageStatus status, int minSeats, Pageable pageable);

    @Query("SELECT p FROM TravelPackage p " +
           "JOIN p.destination d " +
           "WHERE (:destinationId IS NULL OR d.id = :destinationId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:minDuration IS NULL OR p.duration >= :minDuration) " +
           "AND (:maxDuration IS NULL OR p.duration <= :maxDuration) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(p.packageName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(d.country) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TravelPackage> filterPackages(
            @Param("destinationId") Long destinationId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minDuration") Integer minDuration,
            @Param("maxDuration") Integer maxDuration,
            @Param("status") PackageStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);
}
