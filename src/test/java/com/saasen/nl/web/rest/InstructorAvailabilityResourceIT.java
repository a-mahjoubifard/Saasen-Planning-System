package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.InstructorAvailabilityAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.InstructorAvailability;
import com.saasen.nl.repository.InstructorAvailabilityRepository;
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
 * Integration tests for the {@link InstructorAvailabilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InstructorAvailabilityResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_AVAILABLE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_AVAILABLE_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/instructor-availabilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstructorAvailabilityRepository instructorAvailabilityRepository;

    @Mock
    private InstructorAvailabilityRepository instructorAvailabilityRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstructorAvailabilityMockMvc;

    private InstructorAvailability instructorAvailability;

    private InstructorAvailability insertedInstructorAvailability;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstructorAvailability createEntity() {
        return new InstructorAvailability().title(DEFAULT_TITLE).availableFrom(DEFAULT_AVAILABLE_FROM).availableTo(DEFAULT_AVAILABLE_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstructorAvailability createUpdatedEntity() {
        return new InstructorAvailability().title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);
    }

    @BeforeEach
    void initTest() {
        instructorAvailability = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInstructorAvailability != null) {
            instructorAvailabilityRepository.delete(insertedInstructorAvailability);
            insertedInstructorAvailability = null;
        }
    }

    @Test
    @Transactional
    void createInstructorAvailability() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InstructorAvailability
        var returnedInstructorAvailability = om.readValue(
            restInstructorAvailabilityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorAvailability)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstructorAvailability.class
        );

        // Validate the InstructorAvailability in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInstructorAvailabilityUpdatableFieldsEquals(
            returnedInstructorAvailability,
            getPersistedInstructorAvailability(returnedInstructorAvailability)
        );

        insertedInstructorAvailability = returnedInstructorAvailability;
    }

    @Test
    @Transactional
    void createInstructorAvailabilityWithExistingId() throws Exception {
        // Create the InstructorAvailability with an existing ID
        instructorAvailability.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstructorAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorAvailability)))
            .andExpect(status().isBadRequest());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instructorAvailability.setTitle(null);

        // Create the InstructorAvailability, which fails.

        restInstructorAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorAvailability)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInstructorAvailabilities() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        // Get all the instructorAvailabilityList
        restInstructorAvailabilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instructorAvailability.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableTo").value(hasItem(DEFAULT_AVAILABLE_TO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstructorAvailabilitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(instructorAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInstructorAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(instructorAvailabilityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstructorAvailabilitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(instructorAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInstructorAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(instructorAvailabilityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInstructorAvailability() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        // Get the instructorAvailability
        restInstructorAvailabilityMockMvc
            .perform(get(ENTITY_API_URL_ID, instructorAvailability.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instructorAvailability.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.availableFrom").value(DEFAULT_AVAILABLE_FROM.toString()))
            .andExpect(jsonPath("$.availableTo").value(DEFAULT_AVAILABLE_TO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInstructorAvailability() throws Exception {
        // Get the instructorAvailability
        restInstructorAvailabilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstructorAvailability() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorAvailability
        InstructorAvailability updatedInstructorAvailability = instructorAvailabilityRepository
            .findById(instructorAvailability.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInstructorAvailability are not directly saved in db
        em.detach(updatedInstructorAvailability);
        updatedInstructorAvailability.title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restInstructorAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInstructorAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInstructorAvailability))
            )
            .andExpect(status().isOk());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstructorAvailabilityToMatchAllProperties(updatedInstructorAvailability);
    }

    @Test
    @Transactional
    void putNonExistingInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instructorAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructorAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructorAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructorAvailability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstructorAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorAvailability using partial update
        InstructorAvailability partialUpdatedInstructorAvailability = new InstructorAvailability();
        partialUpdatedInstructorAvailability.setId(instructorAvailability.getId());

        partialUpdatedInstructorAvailability.availableTo(UPDATED_AVAILABLE_TO);

        restInstructorAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructorAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructorAvailability))
            )
            .andExpect(status().isOk());

        // Validate the InstructorAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorAvailabilityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstructorAvailability, instructorAvailability),
            getPersistedInstructorAvailability(instructorAvailability)
        );
    }

    @Test
    @Transactional
    void fullUpdateInstructorAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructorAvailability using partial update
        InstructorAvailability partialUpdatedInstructorAvailability = new InstructorAvailability();
        partialUpdatedInstructorAvailability.setId(instructorAvailability.getId());

        partialUpdatedInstructorAvailability.title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restInstructorAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructorAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructorAvailability))
            )
            .andExpect(status().isOk());

        // Validate the InstructorAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorAvailabilityUpdatableFieldsEquals(
            partialUpdatedInstructorAvailability,
            getPersistedInstructorAvailability(partialUpdatedInstructorAvailability)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instructorAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructorAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructorAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstructorAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructorAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(instructorAvailability))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstructorAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstructorAvailability() throws Exception {
        // Initialize the database
        insertedInstructorAvailability = instructorAvailabilityRepository.saveAndFlush(instructorAvailability);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instructorAvailability
        restInstructorAvailabilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, instructorAvailability.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return instructorAvailabilityRepository.count();
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

    protected InstructorAvailability getPersistedInstructorAvailability(InstructorAvailability instructorAvailability) {
        return instructorAvailabilityRepository.findById(instructorAvailability.getId()).orElseThrow();
    }

    protected void assertPersistedInstructorAvailabilityToMatchAllProperties(InstructorAvailability expectedInstructorAvailability) {
        assertInstructorAvailabilityAllPropertiesEquals(
            expectedInstructorAvailability,
            getPersistedInstructorAvailability(expectedInstructorAvailability)
        );
    }

    protected void assertPersistedInstructorAvailabilityToMatchUpdatableProperties(InstructorAvailability expectedInstructorAvailability) {
        assertInstructorAvailabilityAllUpdatablePropertiesEquals(
            expectedInstructorAvailability,
            getPersistedInstructorAvailability(expectedInstructorAvailability)
        );
    }
}
