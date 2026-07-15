package com.saasen.nl.web.rest;

import com.saasen.nl.domain.ResourceAvailability;
import com.saasen.nl.repository.ResourceAvailabilityRepository;
import com.saasen.nl.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.saasen.nl.domain.ResourceAvailability}.
 */
@RestController
@RequestMapping("/api/resource-availabilities")
@Transactional
public class ResourceAvailabilityResource {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceAvailabilityResource.class);

    private static final String ENTITY_NAME = "resourceAvailability";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final ResourceAvailabilityRepository resourceAvailabilityRepository;

    public ResourceAvailabilityResource(ResourceAvailabilityRepository resourceAvailabilityRepository) {
        this.resourceAvailabilityRepository = resourceAvailabilityRepository;
    }

    /**
     * {@code POST  /resource-availabilities} : Create a new resourceAvailability.
     *
     * @param resourceAvailability the resourceAvailability to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceAvailability, or with status {@code 400 (Bad Request)} if the resourceAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ResourceAvailability> createResourceAvailability(@RequestBody ResourceAvailability resourceAvailability)
        throws URISyntaxException {
        LOG.debug("REST request to save ResourceAvailability : {}", resourceAvailability);
        if (resourceAvailability.getId() != null) {
            throw new BadRequestAlertException("A new resourceAvailability cannot already have an ID", ENTITY_NAME, "idexists");
        }
        resourceAvailability = resourceAvailabilityRepository.save(resourceAvailability);
        return ResponseEntity.created(new URI("/api/resource-availabilities/" + resourceAvailability.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, resourceAvailability.getId().toString()))
            .body(resourceAvailability);
    }

    /**
     * {@code PUT  /resource-availabilities/:id} : Updates an existing resourceAvailability.
     *
     * @param id the id of the resourceAvailability to save.
     * @param resourceAvailability the resourceAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceAvailability,
     * or with status {@code 400 (Bad Request)} if the resourceAvailability is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceAvailability> updateResourceAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceAvailability resourceAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to update ResourceAvailability : {}, {}", id, resourceAvailability);
        if (resourceAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        resourceAvailability = resourceAvailabilityRepository.save(resourceAvailability);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceAvailability.getId().toString()))
            .body(resourceAvailability);
    }

    /**
     * {@code PATCH  /resource-availabilities/:id} : Partial updates given fields of an existing resourceAvailability, field will ignore if it is null
     *
     * @param id the id of the resourceAvailability to save.
     * @param resourceAvailability the resourceAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceAvailability,
     * or with status {@code 400 (Bad Request)} if the resourceAvailability is not valid,
     * or with status {@code 404 (Not Found)} if the resourceAvailability is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResourceAvailability> partialUpdateResourceAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceAvailability resourceAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ResourceAvailability partially : {}, {}", id, resourceAvailability);
        if (resourceAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResourceAvailability> result = resourceAvailabilityRepository
            .findById(resourceAvailability.getId())
            .map(existingResourceAvailability -> {
                updateIfPresent(existingResourceAvailability::setAvailableFrom, resourceAvailability.getAvailableFrom());
                updateIfPresent(existingResourceAvailability::setAvailableTo, resourceAvailability.getAvailableTo());

                return existingResourceAvailability;
            })
            .map(resourceAvailabilityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceAvailability.getId().toString())
        );
    }

    /**
     * {@code GET  /resource-availabilities} : get all the Resource Availabilities.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Resource Availabilities in body.
     */
    @GetMapping("")
    public List<ResourceAvailability> getAllResourceAvailabilities(
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("assignedresource-is-null".equals(filter)) {
            LOG.debug("REST request to get all ResourceAvailabilitys where assignedResource is null");
            return StreamSupport.stream(resourceAvailabilityRepository.findAll().spliterator(), false)
                .filter(resourceAvailability -> resourceAvailability.getAssignedResource() == null)
                .toList();
        }
        LOG.debug("REST request to get all ResourceAvailabilities");
        if (eagerload) {
            return resourceAvailabilityRepository.findAllWithEagerRelationships();
        } else {
            return resourceAvailabilityRepository.findAll();
        }
    }

    /**
     * {@code GET  /resource-availabilities/:id} : get the "id" resourceAvailability.
     *
     * @param id the id of the resourceAvailability to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceAvailability, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceAvailability> getResourceAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ResourceAvailability : {}", id);
        Optional<ResourceAvailability> resourceAvailability = resourceAvailabilityRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(resourceAvailability);
    }

    /**
     * {@code DELETE  /resource-availabilities/:id} : delete the "id" resourceAvailability.
     *
     * @param id the id of the resourceAvailability to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ResourceAvailability : {}", id);
        resourceAvailabilityRepository.deleteById(id);
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
