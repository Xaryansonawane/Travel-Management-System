package com.travel.management.repository;

import com.travel.management.entity.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Destination> findByNameIgnoreCase(String name);

    List<Destination> findByCountryIgnoreCase(String country);

    List<Destination> findByStateIgnoreCase(String state);

    @Query("SELECT d FROM Destination d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.country) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.state) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Destination> searchDestinations(@Param("query") String query, Pageable pageable);
}
