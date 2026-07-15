package com.saasen.nl.repository;

import com.saasen.nl.domain.AssignedResource;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssignedResource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssignedResourceRepository extends JpaRepository<AssignedResource, Long> {}
