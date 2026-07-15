package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.LocationAvailabilityAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.LocationAvailability;
import com.saasen.nl.repository.LocationAvailabilityRepository;
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
 * Integration tests for the {@link LocationAvailabilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LocationAvailabilityResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_AVAILABLE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_AVAILABLE_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/location-availabilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocationAvailabilityRepository locationAvailabilityRepository;

    @Mock
    private LocationAvailabilityRepository locationAvailabilityRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocationAvailabilityMockMvc;

    private LocationAvailability locationAvailability;

    private LocationAvailability insertedLocationAvailability;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationAvailability createEntity() {
        return new LocationAvailability().title(DEFAULT_TITLE).availableFrom(DEFAULT_AVAILABLE_FROM).availableTo(DEFAULT_AVAILABLE_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocationAvailability createUpdatedEntity() {
        return new LocationAvailability().title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);
    }

    @BeforeEach
    void initTest() {
        locationAvailability = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLocationAvailability != null) {
            locationAvailabilityRepository.delete(insertedLocationAvailability);
            insertedLocationAvailability = null;
        }
    }

    @Test
    @Transactional
    void createLocationAvailability() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LocationAvailability
        var returnedLocationAvailability = om.readValue(
            restLocationAvailabilityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationAvailability)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocationAvailability.class
        );

        // Validate the LocationAvailability in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLocationAvailabilityUpdatableFieldsEquals(
            returnedLocationAvailability,
            getPersistedLocationAvailability(returnedLocationAvailability)
        );

        insertedLocationAvailability = returnedLocationAvailability;
    }

    @Test
    @Transactional
    void createLocationAvailabilityWithExistingId() throws Exception {
        // Create the LocationAvailability with an existing ID
        locationAvailability.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationAvailability)))
            .andExpect(status().isBadRequest());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locationAvailability.setTitle(null);

        // Create the LocationAvailability, which fails.

        restLocationAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationAvailability)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocationAvailabilities() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        // Get all the locationAvailabilityList
        restLocationAvailabilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationAvailability.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableTo").value(hasItem(DEFAULT_AVAILABLE_TO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationAvailabilitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(locationAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(locationAvailabilityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationAvailabilitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(locationAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(locationAvailabilityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLocationAvailability() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        // Get the locationAvailability
        restLocationAvailabilityMockMvc
            .perform(get(ENTITY_API_URL_ID, locationAvailability.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(locationAvailability.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.availableFrom").value(DEFAULT_AVAILABLE_FROM.toString()))
            .andExpect(jsonPath("$.availableTo").value(DEFAULT_AVAILABLE_TO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLocationAvailability() throws Exception {
        // Get the locationAvailability
        restLocationAvailabilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocationAvailability() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationAvailability
        LocationAvailability updatedLocationAvailability = locationAvailabilityRepository
            .findById(locationAvailability.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedLocationAvailability are not directly saved in db
        em.detach(updatedLocationAvailability);
        updatedLocationAvailability.title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restLocationAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLocationAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLocationAvailability))
            )
            .andExpect(status().isOk());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocationAvailabilityToMatchAllProperties(updatedLocationAvailability);
    }

    @Test
    @Transactional
    void putNonExistingLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locationAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locationAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locationAvailability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocationAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationAvailability using partial update
        LocationAvailability partialUpdatedLocationAvailability = new LocationAvailability();
        partialUpdatedLocationAvailability.setId(locationAvailability.getId());

        restLocationAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationAvailability))
            )
            .andExpect(status().isOk());

        // Validate the LocationAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationAvailabilityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLocationAvailability, locationAvailability),
            getPersistedLocationAvailability(locationAvailability)
        );
    }

    @Test
    @Transactional
    void fullUpdateLocationAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locationAvailability using partial update
        LocationAvailability partialUpdatedLocationAvailability = new LocationAvailability();
        partialUpdatedLocationAvailability.setId(locationAvailability.getId());

        partialUpdatedLocationAvailability.title(UPDATED_TITLE).availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restLocationAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocationAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocationAvailability))
            )
            .andExpect(status().isOk());

        // Validate the LocationAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocationAvailabilityUpdatableFieldsEquals(
            partialUpdatedLocationAvailability,
            getPersistedLocationAvailability(partialUpdatedLocationAvailability)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locationAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locationAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocationAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locationAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationAvailabilityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(locationAvailability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LocationAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocationAvailability() throws Exception {
        // Initialize the database
        insertedLocationAvailability = locationAvailabilityRepository.saveAndFlush(locationAvailability);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the locationAvailability
        restLocationAvailabilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, locationAvailability.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locationAvailabilityRepository.count();
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

    protected LocationAvailability getPersistedLocationAvailability(LocationAvailability locationAvailability) {
        return locationAvailabilityRepository.findById(locationAvailability.getId()).orElseThrow();
    }

    protected void assertPersistedLocationAvailabilityToMatchAllProperties(LocationAvailability expectedLocationAvailability) {
        assertLocationAvailabilityAllPropertiesEquals(
            expectedLocationAvailability,
            getPersistedLocationAvailability(expectedLocationAvailability)
        );
    }

    protected void assertPersistedLocationAvailabilityToMatchUpdatableProperties(LocationAvailability expectedLocationAvailability) {
        assertLocationAvailabilityAllUpdatablePropertiesEquals(
            expectedLocationAvailability,
            getPersistedLocationAvailability(expectedLocationAvailability)
        );
    }
}
