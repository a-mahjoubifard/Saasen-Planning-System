package com.saasen.nl.repository;

import com.saasen.nl.domain.FreelancerAssignmentRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FreelancerAssignmentRequest entity.
 */
@Repository
public interface FreelancerAssignmentRequestRepository extends JpaRepository<FreelancerAssignmentRequest, Long> {
    default Optional<FreelancerAssignmentRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FreelancerAssignmentRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FreelancerAssignmentRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select freelancerAssignmentRequest from FreelancerAssignmentRequest freelancerAssignmentRequest left join fetch freelancerAssignmentRequest.freelancer",
        countQuery = "select count(freelancerAssignmentRequest) from FreelancerAssignmentRequest freelancerAssignmentRequest"
    )
    Page<FreelancerAssignmentRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select freelancerAssignmentRequest from FreelancerAssignmentRequest freelancerAssignmentRequest left join fetch freelancerAssignmentRequest.freelancer"
    )
    List<FreelancerAssignmentRequest> findAllWithToOneRelationships();

    @Query(
        "select freelancerAssignmentRequest from FreelancerAssignmentRequest freelancerAssignmentRequest left join fetch freelancerAssignmentRequest.freelancer where freelancerAssignmentRequest.id =:id"
    )
    Optional<FreelancerAssignmentRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
