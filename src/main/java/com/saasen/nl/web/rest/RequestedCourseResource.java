package com.saasen.nl.web.rest;

import com.saasen.nl.domain.RequestedCourse;
import com.saasen.nl.repository.RequestedCourseRepository;
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
 * REST controller for managing {@link com.saasen.nl.domain.RequestedCourse}.
 */
@RestController
@RequestMapping("/api/requested-courses")
@Transactional
public class RequestedCourseResource {

    private static final Logger LOG = LoggerFactory.getLogger(RequestedCourseResource.class);

    private static final String ENTITY_NAME = "requestedCourse";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final RequestedCourseRepository requestedCourseRepository;

    public RequestedCourseResource(RequestedCourseRepository requestedCourseRepository) {
        this.requestedCourseRepository = requestedCourseRepository;
    }

    /**
     * {@code POST  /requested-courses} : Create a new requestedCourse.
     *
     * @param requestedCourse the requestedCourse to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new requestedCourse, or with status {@code 400 (Bad Request)} if the requestedCourse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RequestedCourse> createRequestedCourse(@Valid @RequestBody RequestedCourse requestedCourse)
        throws URISyntaxException {
        LOG.debug("REST request to save RequestedCourse : {}", requestedCourse);
        if (requestedCourse.getId() != null) {
            throw new BadRequestAlertException("A new requestedCourse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        requestedCourse = requestedCourseRepository.save(requestedCourse);
        return ResponseEntity.created(new URI("/api/requested-courses/" + requestedCourse.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, requestedCourse.getId().toString()))
            .body(requestedCourse);
    }

    /**
     * {@code PUT  /requested-courses/:id} : Updates an existing requestedCourse.
     *
     * @param id the id of the requestedCourse to save.
     * @param requestedCourse the requestedCourse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestedCourse,
     * or with status {@code 400 (Bad Request)} if the requestedCourse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the requestedCourse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RequestedCourse> updateRequestedCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RequestedCourse requestedCourse
    ) throws URISyntaxException {
        LOG.debug("REST request to update RequestedCourse : {}, {}", id, requestedCourse);
        if (requestedCourse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestedCourse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestedCourseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        requestedCourse = requestedCourseRepository.save(requestedCourse);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, requestedCourse.getId().toString()))
            .body(requestedCourse);
    }

    /**
     * {@code PATCH  /requested-courses/:id} : Partial updates given fields of an existing requestedCourse, field will ignore if it is null
     *
     * @param id the id of the requestedCourse to save.
     * @param requestedCourse the requestedCourse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestedCourse,
     * or with status {@code 400 (Bad Request)} if the requestedCourse is not valid,
     * or with status {@code 404 (Not Found)} if the requestedCourse is not found,
     * or with status {@code 500 (Internal Server Error)} if the requestedCourse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RequestedCourse> partialUpdateRequestedCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RequestedCourse requestedCourse
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RequestedCourse partially : {}, {}", id, requestedCourse);
        if (requestedCourse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestedCourse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestedCourseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RequestedCourse> result = requestedCourseRepository
            .findById(requestedCourse.getId())
            .map(existingRequestedCourse -> {
                updateIfPresent(existingRequestedCourse::setNumberOfParticipants, requestedCourse.getNumberOfParticipants());
                updateIfPresent(existingRequestedCourse::setPreferredStartDate, requestedCourse.getPreferredStartDate());
                updateIfPresent(existingRequestedCourse::setPreferredLocation, requestedCourse.getPreferredLocation());
                updateIfPresent(existingRequestedCourse::setStatus, requestedCourse.getStatus());
                updateIfPresent(existingRequestedCourse::setActualStartDate, requestedCourse.getActualStartDate());
                updateIfPresent(existingRequestedCourse::setActualEndDate, requestedCourse.getActualEndDate());

                return existingRequestedCourse;
            })
            .map(requestedCourseRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, requestedCourse.getId().toString())
        );
    }

    /**
     * {@code GET  /requested-courses} : get all the Requested Courses.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Requested Courses in body.
     */
    @GetMapping("")
    public List<RequestedCourse> getAllRequestedCourses(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RequestedCourses");
        if (eagerload) {
            return requestedCourseRepository.findAllWithEagerRelationships();
        } else {
            return requestedCourseRepository.findAll();
        }
    }

    /**
     * {@code GET  /requested-courses/:id} : get the "id" requestedCourse.
     *
     * @param id the id of the requestedCourse to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requestedCourse, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RequestedCourse> getRequestedCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RequestedCourse : {}", id);
        Optional<RequestedCourse> requestedCourse = requestedCourseRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(requestedCourse);
    }

    /**
     * {@code DELETE  /requested-courses/:id} : delete the "id" requestedCourse.
     *
     * @param id the id of the requestedCourse to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequestedCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RequestedCourse : {}", id);
        requestedCourseRepository.deleteById(id);
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
