package com.saasen.nl.web.rest;

import com.saasen.nl.domain.Freelancer;
import com.saasen.nl.repository.FreelancerRepository;
import com.saasen.nl.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.saasen.nl.domain.Freelancer}.
 */
@RestController
@RequestMapping("/api/freelancers")
@Transactional
public class FreelancerResource {

    private static final Logger LOG = LoggerFactory.getLogger(FreelancerResource.class);

    private static final String ENTITY_NAME = "freelancer";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final FreelancerRepository freelancerRepository;

    public FreelancerResource(FreelancerRepository freelancerRepository) {
        this.freelancerRepository = freelancerRepository;
    }

    /**
     * {@code POST  /freelancers} : Create a new freelancer.
     *
     * @param freelancer the freelancer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new freelancer, or with status {@code 400 (Bad Request)} if the freelancer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Freelancer> createFreelancer(@Valid @RequestBody Freelancer freelancer) throws URISyntaxException {
        LOG.debug("REST request to save Freelancer : {}", freelancer);
        if (freelancer.getId() != null) {
            throw new BadRequestAlertException("A new freelancer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        freelancer = freelancerRepository.save(freelancer);
        return ResponseEntity.created(new URI("/api/freelancers/" + freelancer.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, freelancer.getId().toString()))
            .body(freelancer);
    }

    /**
     * {@code PUT  /freelancers/:id} : Updates an existing freelancer.
     *
     * @param id the id of the freelancer to save.
     * @param freelancer the freelancer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated freelancer,
     * or with status {@code 400 (Bad Request)} if the freelancer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the freelancer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Freelancer> updateFreelancer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Freelancer freelancer
    ) throws URISyntaxException {
        LOG.debug("REST request to update Freelancer : {}, {}", id, freelancer);
        if (freelancer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, freelancer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!freelancerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        freelancer = freelancerRepository.save(freelancer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, freelancer.getId().toString()))
            .body(freelancer);
    }

    /**
     * {@code PATCH  /freelancers/:id} : Partial updates given fields of an existing freelancer, field will ignore if it is null
     *
     * @param id the id of the freelancer to save.
     * @param freelancer the freelancer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated freelancer,
     * or with status {@code 400 (Bad Request)} if the freelancer is not valid,
     * or with status {@code 404 (Not Found)} if the freelancer is not found,
     * or with status {@code 500 (Internal Server Error)} if the freelancer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Freelancer> partialUpdateFreelancer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Freelancer freelancer
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Freelancer partially : {}, {}", id, freelancer);
        if (freelancer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, freelancer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!freelancerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Freelancer> result = freelancerRepository
            .findById(freelancer.getId())
            .map(existingFreelancer -> {
                updateIfPresent(existingFreelancer::setName, freelancer.getName());
                updateIfPresent(existingFreelancer::setContact, freelancer.getContact());
                updateIfPresent(existingFreelancer::setQualification, freelancer.getQualification());

                return existingFreelancer;
            })
            .map(freelancerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, freelancer.getId().toString())
        );
    }

    /**
     * {@code GET  /freelancers} : get all the Freelancers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Freelancers in body.
     */
    @GetMapping("")
    public List<Freelancer> getAllFreelancers() {
        LOG.debug("REST request to get all Freelancers");
        return freelancerRepository.findAll();
    }

    /**
     * {@code GET  /freelancers/:id} : get the "id" freelancer.
     *
     * @param id the id of the freelancer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the freelancer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Freelancer> getFreelancer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Freelancer : {}", id);
        Optional<Freelancer> freelancer = freelancerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(freelancer);
    }

    /**
     * {@code DELETE  /freelancers/:id} : delete the "id" freelancer.
     *
     * @param id the id of the freelancer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFreelancer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Freelancer : {}", id);
        freelancerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
