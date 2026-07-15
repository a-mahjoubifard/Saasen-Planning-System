package com.saasen.nl.repository;

import com.saasen.nl.domain.LocationAvailability;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LocationAvailability entity.
 */
@Repository
public interface LocationAvailabilityRepository extends JpaRepository<LocationAvailability, Long> {
    default Optional<LocationAvailability> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LocationAvailability> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LocationAvailability> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select locationAvailability from LocationAvailability locationAvailability left join fetch locationAvailability.location",
        countQuery = "select count(locationAvailability) from LocationAvailability locationAvailability"
    )
    Page<LocationAvailability> findAllWithToOneRelationships(Pageable pageable);

    @Query("select locationAvailability from LocationAvailability locationAvailability left join fetch locationAvailability.location")
    List<LocationAvailability> findAllWithToOneRelationships();

    @Query(
        "select locationAvailability from LocationAvailability locationAvailability left join fetch locationAvailability.location where locationAvailability.id =:id"
    )
    Optional<LocationAvailability> findOneWithToOneRelationships(@Param("id") Long id);
}
