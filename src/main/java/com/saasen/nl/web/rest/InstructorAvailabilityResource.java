package com.saasen.nl.web.rest;

import com.saasen.nl.domain.InstructorAvailability;
import com.saasen.nl.repository.InstructorAvailabilityRepository;
import com.saasen.nl.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.saasen.nl.domain.InstructorAvailability}.
 */
@RestController
@RequestMapping("/api/instructor-availabilities")
@Transactional
public class InstructorAvailabilityResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorAvailabilityResource.class);

    private static final String ENTITY_NAME = "instructorAvailability";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final InstructorAvailabilityRepository instructorAvailabilityRepository;

    public InstructorAvailabilityResource(InstructorAvailabilityRepository instructorAvailabilityRepository) {
        this.instructorAvailabilityRepository = instructorAvailabilityRepository;
    }

    /**
     * {@code POST  /instructor-availabilities} : Create a new instructorAvailability.
     *
     * @param instructorAvailability the instructorAvailability to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instructorAvailability, or with status {@code 400 (Bad Request)} if the instructorAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstructorAvailability> createInstructorAvailability(
        @Valid @RequestBody InstructorAvailability instructorAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to save InstructorAvailability : {}", instructorAvailability);
        if (instructorAvailability.getId() != null) {
            throw new BadRequestAlertException("A new instructorAvailability cannot already have an ID", ENTITY_NAME, "idexists");
        }
        instructorAvailability = instructorAvailabilityRepository.save(instructorAvailability);
        return ResponseEntity.created(new URI("/api/instructor-availabilities/" + instructorAvailability.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, instructorAvailability.getId().toString()))
            .body(instructorAvailability);
    }

    /**
     * {@code PUT  /instructor-availabilities/:id} : Updates an existing instructorAvailability.
     *
     * @param id the id of the instructorAvailability to save.
     * @param instructorAvailability the instructorAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorAvailability,
     * or with status {@code 400 (Bad Request)} if the instructorAvailability is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instructorAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorAvailability> updateInstructorAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstructorAvailability instructorAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to update InstructorAvailability : {}, {}", id, instructorAvailability);
        if (instructorAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructorAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        instructorAvailability = instructorAvailabilityRepository.save(instructorAvailability);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instructorAvailability.getId().toString()))
            .body(instructorAvailability);
    }

    /**
     * {@code PATCH  /instructor-availabilities/:id} : Partial updates given fields of an existing instructorAvailability, field will ignore if it is null
     *
     * @param id the id of the instructorAvailability to save.
     * @param instructorAvailability the instructorAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorAvailability,
     * or with status {@code 400 (Bad Request)} if the instructorAvailability is not valid,
     * or with status {@code 404 (Not Found)} if the instructorAvailability is not found,
     * or with status {@code 500 (Internal Server Error)} if the instructorAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstructorAvailability> partialUpdateInstructorAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstructorAvailability instructorAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InstructorAvailability partially : {}, {}", id, instructorAvailability);
        if (instructorAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructorAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstructorAvailability> result = instructorAvailabilityRepository
            .findById(instructorAvailability.getId())
            .map(existingInstructorAvailability -> {
                updateIfPresent(existingInstructorAvailability::setTitle, instructorAvailability.getTitle());
                updateIfPresent(existingInstructorAvailability::setAvailableFrom, instructorAvailability.getAvailableFrom());
                updateIfPresent(existingInstructorAvailability::setAvailableTo, instructorAvailability.getAvailableTo());

                return existingInstructorAvailability;
            })
            .map(instructorAvailabilityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instructorAvailability.getId().toString())
        );
    }

    /**
     * {@code GET  /instructor-availabilities} : get all the Instructor Availabilities.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Instructor Availabilities in body.
     */
    @GetMapping("")
    public List<InstructorAvailability> getAllInstructorAvailabilities(
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("session-is-null".equals(filter)) {
            LOG.debug("REST request to get all InstructorAvailabilitys where session is null");
            return StreamSupport.stream(instructorAvailabilityRepository.findAll().spliterator(), false)
                .filter(instructorAvailability -> instructorAvailability.getSession() == null)
                .toList();
        }
        LOG.debug("REST request to get all InstructorAvailabilities");
        if (eagerload) {
            return instructorAvailabilityRepository.findAllWithEagerRelationships();
        } else {
            return instructorAvailabilityRepository.findAll();
        }
    }

    /**
     * {@code GET  /instructor-availabilities/:id} : get the "id" instructorAvailability.
     *
     * @param id the id of the instructorAvailability to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instructorAvailability, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorAvailability> getInstructorAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InstructorAvailability : {}", id);
        Optional<InstructorAvailability> instructorAvailability = instructorAvailabilityRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(instructorAvailability);
    }

    /**
     * {@code DELETE  /instructor-availabilities/:id} : delete the "id" instructorAvailability.
     *
     * @param id the id of the instructorAvailability to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructorAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InstructorAvailability : {}", id);
        instructorAvailabilityRepository.deleteById(id);
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
