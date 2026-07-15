package com.saasen.nl.repository;

import com.saasen.nl.domain.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Session entity.
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    default Optional<Session> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Session> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Session> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select session from Session session left join fetch session.instructorAvailability left join fetch session.locationAvailability left join fetch session.freelancer",
        countQuery = "select count(session) from Session session"
    )
    Page<Session> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select session from Session session left join fetch session.instructorAvailability left join fetch session.locationAvailability left join fetch session.freelancer"
    )
    List<Session> findAllWithToOneRelationships();

    @Query(
        "select session from Session session left join fetch session.instructorAvailability left join fetch session.locationAvailability left join fetch session.freelancer where session.id =:id"
    )
    Optional<Session> findOneWithToOneRelationships(@Param("id") Long id);
}
