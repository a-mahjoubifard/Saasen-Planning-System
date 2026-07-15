package com.saasen.nl.web.rest;

import com.saasen.nl.domain.Session;
import com.saasen.nl.repository.SessionRepository;
import com.saasen.nl.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.saasen.nl.domain.Session}.
 */
@RestController
@RequestMapping("/api/sessions")
@Transactional
public class SessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SessionResource.class);

    private static final String ENTITY_NAME = "session";

    @Value("${jhipster.clientApp.name:saasen}")
    private String applicationName;

    private final SessionRepository sessionRepository;

    public SessionResource(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * {@code POST  /sessions} : Create a new session.
     *
     * @param session the session to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new session, or with status {@code 400 (Bad Request)} if the session has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Session> createSession(@RequestBody Session session) throws URISyntaxException {
        LOG.debug("REST request to save Session : {}", session);
        if (session.getId() != null) {
            throw new BadRequestAlertException("A new session cannot already have an ID", ENTITY_NAME, "idexists");
        }
        session = sessionRepository.save(session);
        return ResponseEntity.created(new URI("/api/sessions/" + session.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, session.getId().toString()))
            .body(session);
    }

    /**
     * {@code PUT  /sessions/:id} : Updates an existing session.
     *
     * @param id the id of the session to save.
     * @param session the session to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated session,
     * or with status {@code 400 (Bad Request)} if the session is not valid,
     * or with status {@code 500 (Internal Server Error)} if the session couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable(value = "id", required = false) final Long id, @RequestBody Session session)
        throws URISyntaxException {
        LOG.debug("REST request to update Session : {}, {}", id, session);
        if (session.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, session.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        session = sessionRepository.save(session);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, session.getId().toString()))
            .body(session);
    }

    /**
     * {@code PATCH  /sessions/:id} : Partial updates given fields of an existing session, field will ignore if it is null
     *
     * @param id the id of the session to save.
     * @param session the session to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated session,
     * or with status {@code 400 (Bad Request)} if the session is not valid,
     * or with status {@code 404 (Not Found)} if the session is not found,
     * or with status {@code 500 (Internal Server Error)} if the session couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Session> partialUpdateSession(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Session session
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Session partially : {}, {}", id, session);
        if (session.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, session.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Session> result = sessionRepository
            .findById(session.getId())
            .map(existingSession -> {
                updateIfPresent(existingSession::setStartDate, session.getStartDate());
                updateIfPresent(existingSession::setEndDate, session.getEndDate());
                updateIfPresent(existingSession::setStatus, session.getStatus());

                return existingSession;
            })
            .map(sessionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, session.getId().toString())
        );
    }

    /**
     * {@code GET  /sessions} : get all the Sessions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Sessions in body.
     */
    @GetMapping("")
    public List<Session> getAllSessions(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Sessions");
        if (eagerload) {
            return sessionRepository.findAllWithEagerRelationships();
        } else {
            return sessionRepository.findAll();
        }
    }

    /**
     * {@code GET  /sessions/:id} : get the "id" session.
     *
     * @param id the id of the session to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the session, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Session : {}", id);
        Optional<Session> session = sessionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(session);
    }

    /**
     * {@code DELETE  /sessions/:id} : delete the "id" session.
     *
     * @param id the id of the session to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Session : {}", id);
        sessionRepository.deleteById(id);
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
