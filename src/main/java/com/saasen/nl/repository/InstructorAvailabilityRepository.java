package com.saasen.nl.repository;

import com.saasen.nl.domain.InstructorAvailability;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InstructorAvailability entity.
 */
@Repository
public interface InstructorAvailabilityRepository extends JpaRepository<InstructorAvailability, Long> {
    default Optional<InstructorAvailability> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InstructorAvailability> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InstructorAvailability> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select instructorAvailability from InstructorAvailability instructorAvailability left join fetch instructorAvailability.instructor",
        countQuery = "select count(instructorAvailability) from InstructorAvailability instructorAvailability"
    )
    Page<InstructorAvailability> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select instructorAvailability from InstructorAvailability instructorAvailability left join fetch instructorAvailability.instructor"
    )
    List<InstructorAvailability> findAllWithToOneRelationships();

    @Query(
        "select instructorAvailability from InstructorAvailability instructorAvailability left join fetch instructorAvailability.instructor where instructorAvailability.id =:id"
    )
    Optional<InstructorAvailability> findOneWithToOneRelationships(@Param("id") Long id);
}
