package com.saasen.nl.web.rest;

import com.saasen.nl.domain.AssignedResource;
import com.saasen.nl.repository.AssignedResourceRepository;
import com.saasen.nl.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.saasen.nl.domain.AssignedResource}.
 */
@RestController
@RequestMapping("/api/assigned-resources")
@Transactional
public class AssignedResourceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssignedResourceResource.class);

    private static final String ENTITY_NAME = "assignedResource";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final AssignedResourceRepository assignedResourceRepository;

    public AssignedResourceResource(AssignedResourceRepository assignedResourceRepository) {
        this.assignedResourceRepository = assignedResourceRepository;
    }

    /**
     * {@code POST  /assigned-resources} : Create a new assignedResource.
     *
     * @param assignedResource the assignedResource to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assignedResource, or with status {@code 400 (Bad Request)} if the assignedResource has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssignedResource> createAssignedResource(@RequestBody AssignedResource assignedResource)
        throws URISyntaxException {
        LOG.debug("REST request to save AssignedResource : {}", assignedResource);
        if (assignedResource.getId() != null) {
            throw new BadRequestAlertException("A new assignedResource cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assignedResource = assignedResourceRepository.save(assignedResource);
        return ResponseEntity.created(new URI("/api/assigned-resources/" + assignedResource.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, assignedResource.getId().toString()))
            .body(assignedResource);
    }

    /**
     * {@code PUT  /assigned-resources/:id} : Updates an existing assignedResource.
     *
     * @param id the id of the assignedResource to save.
     * @param assignedResource the assignedResource to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignedResource,
     * or with status {@code 400 (Bad Request)} if the assignedResource is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assignedResource couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssignedResource> updateAssignedResource(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AssignedResource assignedResource
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssignedResource : {}, {}", id, assignedResource);
        if (assignedResource.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignedResource.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignedResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assignedResource = assignedResourceRepository.save(assignedResource);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, assignedResource.getId().toString()))
            .body(assignedResource);
    }

    /**
     * {@code PATCH  /assigned-resources/:id} : Partial updates given fields of an existing assignedResource, field will ignore if it is null
     *
     * @param id the id of the assignedResource to save.
     * @param assignedResource the assignedResource to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignedResource,
     * or with status {@code 400 (Bad Request)} if the assignedResource is not valid,
     * or with status {@code 404 (Not Found)} if the assignedResource is not found,
     * or with status {@code 500 (Internal Server Error)} if the assignedResource couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssignedResource> partialUpdateAssignedResource(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AssignedResource assignedResource
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssignedResource partially : {}, {}", id, assignedResource);
        if (assignedResource.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignedResource.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignedResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssignedResource> result = assignedResourceRepository
            .findById(assignedResource.getId())
            .map(assignedResourceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, assignedResource.getId().toString())
        );
    }

    /**
     * {@code GET  /assigned-resources} : get all the Assigned Resources.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Assigned Resources in body.
     */
    @GetMapping("")
    public List<AssignedResource> getAllAssignedResources() {
        LOG.debug("REST request to get all AssignedResources");
        return assignedResourceRepository.findAll();
    }

    /**
     * {@code GET  /assigned-resources/:id} : get the "id" assignedResource.
     *
     * @param id the id of the assignedResource to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assignedResource, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignedResource> getAssignedResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssignedResource : {}", id);
        Optional<AssignedResource> assignedResource = assignedResourceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(assignedResource);
    }

    /**
     * {@code DELETE  /assigned-resources/:id} : delete the "id" assignedResource.
     *
     * @param id the id of the assignedResource to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignedResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssignedResource : {}", id);
        assignedResourceRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
