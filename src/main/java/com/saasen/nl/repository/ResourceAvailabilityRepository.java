package com.saasen.nl.repository;

import com.saasen.nl.domain.ResourceAvailability;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ResourceAvailability entity.
 */
@Repository
public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, Long> {
    default Optional<ResourceAvailability> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ResourceAvailability> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ResourceAvailability> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select resourceAvailability from ResourceAvailability resourceAvailability left join fetch resourceAvailability.resource",
        countQuery = "select count(resourceAvailability) from ResourceAvailability resourceAvailability"
    )
    Page<ResourceAvailability> findAllWithToOneRelationships(Pageable pageable);

    @Query("select resourceAvailability from ResourceAvailability resourceAvailability left join fetch resourceAvailability.resource")
    List<ResourceAvailability> findAllWithToOneRelationships();

    @Query(
        "select resourceAvailability from ResourceAvailability resourceAvailability left join fetch resourceAvailability.resource where resourceAvailability.id =:id"
    )
    Optional<ResourceAvailability> findOneWithToOneRelationships(@Param("id") Long id);
}
