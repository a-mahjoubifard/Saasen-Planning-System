package com.saasen.nl.repository;

import com.saasen.nl.domain.TrainingRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TrainingRequest entity.
 */
@Repository
public interface TrainingRequestRepository extends JpaRepository<TrainingRequest, Long> {
    default Optional<TrainingRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TrainingRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TrainingRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select trainingRequest from TrainingRequest trainingRequest left join fetch trainingRequest.customer",
        countQuery = "select count(trainingRequest) from TrainingRequest trainingRequest"
    )
    Page<TrainingRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query("select trainingRequest from TrainingRequest trainingRequest left join fetch trainingRequest.customer")
    List<TrainingRequest> findAllWithToOneRelationships();

    @Query(
        "select trainingRequest from TrainingRequest trainingRequest left join fetch trainingRequest.customer where trainingRequest.id =:id"
    )
    Optional<TrainingRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
