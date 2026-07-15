package com.saasen.nl.repository;

import com.saasen.nl.domain.RequestedCourse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RequestedCourse entity.
 */
@Repository
public interface RequestedCourseRepository extends JpaRepository<RequestedCourse, Long> {
    default Optional<RequestedCourse> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RequestedCourse> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RequestedCourse> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select requestedCourse from RequestedCourse requestedCourse left join fetch requestedCourse.course",
        countQuery = "select count(requestedCourse) from RequestedCourse requestedCourse"
    )
    Page<RequestedCourse> findAllWithToOneRelationships(Pageable pageable);

    @Query("select requestedCourse from RequestedCourse requestedCourse left join fetch requestedCourse.course")
    List<RequestedCourse> findAllWithToOneRelationships();

    @Query(
        "select requestedCourse from RequestedCourse requestedCourse left join fetch requestedCourse.course where requestedCourse.id =:id"
    )
    Optional<RequestedCourse> findOneWithToOneRelationships(@Param("id") Long id);
}
