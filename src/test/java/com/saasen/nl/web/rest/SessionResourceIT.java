package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.SessionAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.Session;
import com.saasen.nl.domain.enumeration.RequestStatus;
import com.saasen.nl.repository.SessionRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SessionResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final RequestStatus DEFAULT_STATUS = RequestStatus.PENDING;
    private static final RequestStatus UPDATED_STATUS = RequestStatus.PLANNING;

    private static final String ENTITY_API_URL = "/api/sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SessionRepository sessionRepository;

    @Mock
    private SessionRepository sessionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionMockMvc;

    private Session session;

    private Session insertedSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createEntity() {
        return new Session().startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createUpdatedEntity() {
        return new Session().startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        session = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSession != null) {
            sessionRepository.delete(insertedSession);
            insertedSession = null;
        }
    }

    @Test
    @Transactional
    void createSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Session
        var returnedSession = om.readValue(
            restSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(session)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Session.class
        );

        // Validate the Session in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSessionUpdatableFieldsEquals(returnedSession, getPersistedSession(returnedSession));

        insertedSession = returnedSession;
    }

    @Test
    @Transactional
    void createSessionWithExistingId() throws Exception {
        // Create the Session with an existing ID
        session.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(session)))
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSessions() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        // Get all the sessionList
        restSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sessionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sessionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sessionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        // Get the session
        restSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, session.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(session.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSession() throws Exception {
        // Get the session
        restSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSession are not directly saved in db
        em.detach(updatedSession);
        updatedSession.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSessionToMatchAllProperties(updatedSession);
    }

    @Test
    @Transactional
    void putNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(put(ENTITY_API_URL_ID, session.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(session)))
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(session)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession.startDate(UPDATED_START_DATE);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSession, session), getPersistedSession(session));
    }

    @Test
    @Transactional
    void fullUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(partialUpdatedSession, getPersistedSession(partialUpdatedSession));
    }

    @Test
    @Transactional
    void patchNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, session.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(session)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the session
        restSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, session.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sessionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Session getPersistedSession(Session session) {
        return sessionRepository.findById(session.getId()).orElseThrow();
    }

    protected void assertPersistedSessionToMatchAllProperties(Session expectedSession) {
        assertSessionAllPropertiesEquals(expectedSession, getPersistedSession(expectedSession));
    }

    protected void assertPersistedSessionToMatchUpdatableProperties(Session expectedSession) {
        assertSessionAllUpdatablePropertiesEquals(expectedSession, getPersistedSession(expectedSession));
    }
}
