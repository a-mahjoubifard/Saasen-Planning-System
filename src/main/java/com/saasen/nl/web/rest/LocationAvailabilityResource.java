package com.saasen.nl.web.rest;

import com.saasen.nl.domain.LocationAvailability;
import com.saasen.nl.repository.LocationAvailabilityRepository;
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
 * REST controller for managing {@link com.saasen.nl.domain.LocationAvailability}.
 */
@RestController
@RequestMapping("/api/location-availabilities")
@Transactional
public class LocationAvailabilityResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocationAvailabilityResource.class);

    private static final String ENTITY_NAME = "locationAvailability";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final LocationAvailabilityRepository locationAvailabilityRepository;

    public LocationAvailabilityResource(LocationAvailabilityRepository locationAvailabilityRepository) {
        this.locationAvailabilityRepository = locationAvailabilityRepository;
    }

    /**
     * {@code POST  /location-availabilities} : Create a new locationAvailability.
     *
     * @param locationAvailability the locationAvailability to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locationAvailability, or with status {@code 400 (Bad Request)} if the locationAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LocationAvailability> createLocationAvailability(@Valid @RequestBody LocationAvailability locationAvailability)
        throws URISyntaxException {
        LOG.debug("REST request to save LocationAvailability : {}", locationAvailability);
        if (locationAvailability.getId() != null) {
            throw new BadRequestAlertException("A new locationAvailability cannot already have an ID", ENTITY_NAME, "idexists");
        }
        locationAvailability = locationAvailabilityRepository.save(locationAvailability);
        return ResponseEntity.created(new URI("/api/location-availabilities/" + locationAvailability.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, locationAvailability.getId().toString()))
            .body(locationAvailability);
    }

    /**
     * {@code PUT  /location-availabilities/:id} : Updates an existing locationAvailability.
     *
     * @param id the id of the locationAvailability to save.
     * @param locationAvailability the locationAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationAvailability,
     * or with status {@code 400 (Bad Request)} if the locationAvailability is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locationAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationAvailability> updateLocationAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocationAvailability locationAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to update LocationAvailability : {}, {}", id, locationAvailability);
        if (locationAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        locationAvailability = locationAvailabilityRepository.save(locationAvailability);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, locationAvailability.getId().toString()))
            .body(locationAvailability);
    }

    /**
     * {@code PATCH  /location-availabilities/:id} : Partial updates given fields of an existing locationAvailability, field will ignore if it is null
     *
     * @param id the id of the locationAvailability to save.
     * @param locationAvailability the locationAvailability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationAvailability,
     * or with status {@code 400 (Bad Request)} if the locationAvailability is not valid,
     * or with status {@code 404 (Not Found)} if the locationAvailability is not found,
     * or with status {@code 500 (Internal Server Error)} if the locationAvailability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LocationAvailability> partialUpdateLocationAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocationAvailability locationAvailability
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LocationAvailability partially : {}, {}", id, locationAvailability);
        if (locationAvailability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationAvailability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LocationAvailability> result = locationAvailabilityRepository
            .findById(locationAvailability.getId())
            .map(existingLocationAvailability -> {
                updateIfPresent(existingLocationAvailability::setTitle, locationAvailability.getTitle());
                updateIfPresent(existingLocationAvailability::setAvailableFrom, locationAvailability.getAvailableFrom());
                updateIfPresent(existingLocationAvailability::setAvailableTo, locationAvailability.getAvailableTo());

                return existingLocationAvailability;
            })
            .map(locationAvailabilityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, locationAvailability.getId().toString())
        );
    }

    /**
     * {@code GET  /location-availabilities} : get all the Location Availabilities.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Location Availabilities in body.
     */
    @GetMapping("")
    public List<LocationAvailability> getAllLocationAvailabilities(
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("session-is-null".equals(filter)) {
            LOG.debug("REST request to get all LocationAvailabilitys where session is null");
            return StreamSupport.stream(locationAvailabilityRepository.findAll().spliterator(), false)
                .filter(locationAvailability -> locationAvailability.getSession() == null)
                .toList();
        }
        LOG.debug("REST request to get all LocationAvailabilities");
        if (eagerload) {
            return locationAvailabilityRepository.findAllWithEagerRelationships();
        } else {
            return locationAvailabilityRepository.findAll();
        }
    }

    /**
     * {@code GET  /location-availabilities/:id} : get the "id" locationAvailability.
     *
     * @param id the id of the locationAvailability to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locationAvailability, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationAvailability> getLocationAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LocationAvailability : {}", id);
        Optional<LocationAvailability> locationAvailability = locationAvailabilityRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(locationAvailability);
    }

    /**
     * {@code DELETE  /location-availabilities/:id} : delete the "id" locationAvailability.
     *
     * @param id the id of the locationAvailability to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationAvailability(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LocationAvailability : {}", id);
        locationAvailabilityRepository.deleteById(id);
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
