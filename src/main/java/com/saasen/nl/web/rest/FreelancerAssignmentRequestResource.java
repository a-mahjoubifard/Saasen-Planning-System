package com.saasen.nl.web.rest;

import com.saasen.nl.domain.FreelancerAssignmentRequest;
import com.saasen.nl.repository.FreelancerAssignmentRequestRepository;
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
 * REST controller for managing {@link com.saasen.nl.domain.FreelancerAssignmentRequest}.
 */
@RestController
@RequestMapping("/api/freelancer-assignment-requests")
@Transactional
public class FreelancerAssignmentRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(FreelancerAssignmentRequestResource.class);

    private static final String ENTITY_NAME = "freelancerAssignmentRequest";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final FreelancerAssignmentRequestRepository freelancerAssignmentRequestRepository;

    public FreelancerAssignmentRequestResource(FreelancerAssignmentRequestRepository freelancerAssignmentRequestRepository) {
        this.freelancerAssignmentRequestRepository = freelancerAssignmentRequestRepository;
    }

    /**
     * {@code POST  /freelancer-assignment-requests} : Create a new freelancerAssignmentRequest.
     *
     * @param freelancerAssignmentRequest the freelancerAssignmentRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new freelancerAssignmentRequest, or with status {@code 400 (Bad Request)} if the freelancerAssignmentRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FreelancerAssignmentRequest> createFreelancerAssignmentRequest(
        @Valid @RequestBody FreelancerAssignmentRequest freelancerAssignmentRequest
    ) throws URISyntaxException {
        LOG.debug("REST request to save FreelancerAssignmentRequest : {}", freelancerAssignmentRequest);
        if (freelancerAssignmentRequest.getId() != null) {
            throw new BadRequestAlertException("A new freelancerAssignmentRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        freelancerAssignmentRequest = freelancerAssignmentRequestRepository.save(freelancerAssignmentRequest);
        return ResponseEntity.created(new URI("/api/freelancer-assignment-requests/" + freelancerAssignmentRequest.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, freelancerAssignmentRequest.getId().toString())
            )
            .body(freelancerAssignmentRequest);
    }

    /**
     * {@code PUT  /freelancer-assignment-requests/:id} : Updates an existing freelancerAssignmentRequest.
     *
     * @param id the id of the freelancerAssignmentRequest to save.
     * @param freelancerAssignmentRequest the freelancerAssignmentRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated freelancerAssignmentRequest,
     * or with status {@code 400 (Bad Request)} if the freelancerAssignmentRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the freelancerAssignmentRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FreelancerAssignmentRequest> updateFreelancerAssignmentRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FreelancerAssignmentRequest freelancerAssignmentRequest
    ) throws URISyntaxException {
        LOG.debug("REST request to update FreelancerAssignmentRequest : {}, {}", id, freelancerAssignmentRequest);
        if (freelancerAssignmentRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, freelancerAssignmentRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!freelancerAssignmentRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        freelancerAssignmentRequest = freelancerAssignmentRequestRepository.save(freelancerAssignmentRequest);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, freelancerAssignmentRequest.getId().toString())
            )
            .body(freelancerAssignmentRequest);
    }

    /**
     * {@code PATCH  /freelancer-assignment-requests/:id} : Partial updates given fields of an existing freelancerAssignmentRequest, field will ignore if it is null
     *
     * @param id the id of the freelancerAssignmentRequest to save.
     * @param freelancerAssignmentRequest the freelancerAssignmentRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated freelancerAssignmentRequest,
     * or with status {@code 400 (Bad Request)} if the freelancerAssignmentRequest is not valid,
     * or with status {@code 404 (Not Found)} if the freelancerAssignmentRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the freelancerAssignmentRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FreelancerAssignmentRequest> partialUpdateFreelancerAssignmentRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FreelancerAssignmentRequest freelancerAssignmentRequest
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FreelancerAssignmentRequest partially : {}, {}", id, freelancerAssignmentRequest);
        if (freelancerAssignmentRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, freelancerAssignmentRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!freelancerAssignmentRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FreelancerAssignmentRequest> result = freelancerAssignmentRequestRepository
            .findById(freelancerAssignmentRequest.getId())
            .map(existingFreelancerAssignmentRequest -> {
                updateIfPresent(existingFreelancerAssignmentRequest::setRequestedAt, freelancerAssignmentRequest.getRequestedAt());
                updateIfPresent(existingFreelancerAssignmentRequest::setStatus, freelancerAssignmentRequest.getStatus());

                return existingFreelancerAssignmentRequest;
            })
            .map(freelancerAssignmentRequestRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, freelancerAssignmentRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /freelancer-assignment-requests} : get all the Freelancer Assignment Requests.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Freelancer Assignment Requests in body.
     */
    @GetMapping("")
    public List<FreelancerAssignmentRequest> getAllFreelancerAssignmentRequests(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all FreelancerAssignmentRequests");
        if (eagerload) {
            return freelancerAssignmentRequestRepository.findAllWithEagerRelationships();
        } else {
            return freelancerAssignmentRequestRepository.findAll();
        }
    }

    /**
     * {@code GET  /freelancer-assignment-requests/:id} : get the "id" freelancerAssignmentRequest.
     *
     * @param id the id of the freelancerAssignmentRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the freelancerAssignmentRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FreelancerAssignmentRequest> getFreelancerAssignmentRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FreelancerAssignmentRequest : {}", id);
        Optional<FreelancerAssignmentRequest> freelancerAssignmentRequest =
            freelancerAssignmentRequestRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(freelancerAssignmentRequest);
    }

    /**
     * {@code DELETE  /freelancer-assignment-requests/:id} : delete the "id" freelancerAssignmentRequest.
     *
     * @param id the id of the freelancerAssignmentRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFreelancerAssignmentRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FreelancerAssignmentRequest : {}", id);
        freelancerAssignmentRequestRepository.deleteById(id);
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
