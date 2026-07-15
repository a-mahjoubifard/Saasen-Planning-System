package com.saasen.nl.web.rest;

import com.saasen.nl.domain.TrainingRequest;
import com.saasen.nl.repository.TrainingRequestRepository;
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
 * REST controller for managing {@link com.saasen.nl.domain.TrainingRequest}.
 */
@RestController
@RequestMapping("/api/training-requests")
@Transactional
public class TrainingRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingRequestResource.class);

    private static final String ENTITY_NAME = "trainingRequest";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final TrainingRequestRepository trainingRequestRepository;

    public TrainingRequestResource(TrainingRequestRepository trainingRequestRepository) {
        this.trainingRequestRepository = trainingRequestRepository;
    }

    /**
     * {@code POST  /training-requests} : Create a new trainingRequest.
     *
     * @param trainingRequest the trainingRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trainingRequest, or with status {@code 400 (Bad Request)} if the trainingRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TrainingRequest> createTrainingRequest(@Valid @RequestBody TrainingRequest trainingRequest)
        throws URISyntaxException {
        LOG.debug("REST request to save TrainingRequest : {}", trainingRequest);
        if (trainingRequest.getId() != null) {
            throw new BadRequestAlertException("A new trainingRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        trainingRequest = trainingRequestRepository.save(trainingRequest);
        return ResponseEntity.created(new URI("/api/training-requests/" + trainingRequest.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, trainingRequest.getId().toString()))
            .body(trainingRequest);
    }

    /**
     * {@code PUT  /training-requests/:id} : Updates an existing trainingRequest.
     *
     * @param id the id of the trainingRequest to save.
     * @param trainingRequest the trainingRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trainingRequest,
     * or with status {@code 400 (Bad Request)} if the trainingRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trainingRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrainingRequest> updateTrainingRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrainingRequest trainingRequest
    ) throws URISyntaxException {
        LOG.debug("REST request to update TrainingRequest : {}, {}", id, trainingRequest);
        if (trainingRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trainingRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trainingRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        trainingRequest = trainingRequestRepository.save(trainingRequest);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, trainingRequest.getId().toString()))
            .body(trainingRequest);
    }

    /**
     * {@code PATCH  /training-requests/:id} : Partial updates given fields of an existing trainingRequest, field will ignore if it is null
     *
     * @param id the id of the trainingRequest to save.
     * @param trainingRequest the trainingRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trainingRequest,
     * or with status {@code 400 (Bad Request)} if the trainingRequest is not valid,
     * or with status {@code 404 (Not Found)} if the trainingRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the trainingRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TrainingRequest> partialUpdateTrainingRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrainingRequest trainingRequest
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TrainingRequest partially : {}, {}", id, trainingRequest);
        if (trainingRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trainingRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trainingRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TrainingRequest> result = trainingRequestRepository
            .findById(trainingRequest.getId())
            .map(existingTrainingRequest -> {
                updateIfPresent(existingTrainingRequest::setRequestDate, trainingRequest.getRequestDate());
                updateIfPresent(existingTrainingRequest::setDescription, trainingRequest.getDescription());

                return existingTrainingRequest;
            })
            .map(trainingRequestRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, trainingRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /training-requests} : get all the Training Requests.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Training Requests in body.
     */
    @GetMapping("")
    public List<TrainingRequest> getAllTrainingRequests(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TrainingRequests");
        if (eagerload) {
            return trainingRequestRepository.findAllWithEagerRelationships();
        } else {
            return trainingRequestRepository.findAll();
        }
    }

    /**
     * {@code GET  /training-requests/:id} : get the "id" trainingRequest.
     *
     * @param id the id of the trainingRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trainingRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainingRequest> getTrainingRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TrainingRequest : {}", id);
        Optional<TrainingRequest> trainingRequest = trainingRequestRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(trainingRequest);
    }

    /**
     * {@code DELETE  /training-requests/:id} : delete the "id" trainingRequest.
     *
     * @param id the id of the trainingRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TrainingRequest : {}", id);
        trainingRequestRepository.deleteById(id);
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
