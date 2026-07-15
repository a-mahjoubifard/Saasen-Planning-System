package com.saasen.nl.repository;

import com.saasen.nl.domain.Freelancer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Freelancer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {}
