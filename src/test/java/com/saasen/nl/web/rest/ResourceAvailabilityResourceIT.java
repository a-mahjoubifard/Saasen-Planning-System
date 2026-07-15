package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.ResourceAvailabilityAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.ResourceAvailability;
import com.saasen.nl.repository.ResourceAvailabilityRepository;
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
 * Integration tests for the {@link ResourceAvailabilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ResourceAvailabilityResourceIT {

    private static final Instant DEFAULT_AVAILABLE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_AVAILABLE_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/resource-availabilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ResourceAvailabilityRepository resourceAvailabilityRepository;

    @Mock
    private ResourceAvailabilityRepository resourceAvailabilityRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceAvailabilityMockMvc;

    private ResourceAvailability resourceAvailability;

    private ResourceAvailability insertedResourceAvailability;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceAvailability createEntity() {
        return new ResourceAvailability().availableFrom(DEFAULT_AVAILABLE_FROM).availableTo(DEFAULT_AVAILABLE_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceAvailability createUpdatedEntity() {
        return new ResourceAvailability().availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);
    }

    @BeforeEach
    void initTest() {
        resourceAvailability = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedResourceAvailability != null) {
            resourceAvailabilityRepository.delete(insertedResourceAvailability);
            insertedResourceAvailability = null;
        }
    }

    @Test
    @Transactional
    void createResourceAvailability() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ResourceAvailability
        var returnedResourceAvailability = om.readValue(
            restResourceAvailabilityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceAvailability)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ResourceAvailability.class
        );

        // Validate the ResourceAvailability in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertResourceAvailabilityUpdatableFieldsEquals(
            returnedResourceAvailability,
            getPersistedResourceAvailability(returnedResourceAvailability)
        );

        insertedResourceAvailability = returnedResourceAvailability;
    }

    @Test
    @Transactional
    void createResourceAvailabilityWithExistingId() throws Exception {
        // Create the ResourceAvailability with an existing ID
        resourceAvailability.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceAvailability)))
            .andExpect(status().isBadRequest());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllResourceAvailabilities() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        // Get all the resourceAvailabilityList
        restResourceAvailabilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceAvailability.getId().intValue())))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableTo").value(hasItem(DEFAULT_AVAILABLE_TO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResourceAvailabilitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(resourceAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResourceAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(resourceAvailabilityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResourceAvailabilitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(resourceAvailabilityRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResourceAvailabilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(resourceAvailabilityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getResourceAvailability() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        // Get the resourceAvailability
        restResourceAvailabilityMockMvc
            .perform(get(ENTITY_API_URL_ID, resourceAvailability.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resourceAvailability.getId().intValue()))
            .andExpect(jsonPath("$.availableFrom").value(DEFAULT_AVAILABLE_FROM.toString()))
            .andExpect(jsonPath("$.availableTo").value(DEFAULT_AVAILABLE_TO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingResourceAvailability() throws Exception {
        // Get the resourceAvailability
        restResourceAvailabilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResourceAvailability() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceAvailability
        ResourceAvailability updatedResourceAvailability = resourceAvailabilityRepository
            .findById(resourceAvailability.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedResourceAvailability are not directly saved in db
        em.detach(updatedResourceAvailability);
        updatedResourceAvailability.availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restResourceAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResourceAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedResourceAvailability))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedResourceAvailabilityToMatchAllProperties(updatedResourceAvailability);
    }

    @Test
    @Transactional
    void putNonExistingResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(resourceAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(resourceAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceAvailability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResourceAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceAvailability using partial update
        ResourceAvailability partialUpdatedResourceAvailability = new ResourceAvailability();
        partialUpdatedResourceAvailability.setId(resourceAvailability.getId());

        partialUpdatedResourceAvailability.availableTo(UPDATED_AVAILABLE_TO);

        restResourceAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResourceAvailability))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResourceAvailabilityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedResourceAvailability, resourceAvailability),
            getPersistedResourceAvailability(resourceAvailability)
        );
    }

    @Test
    @Transactional
    void fullUpdateResourceAvailabilityWithPatch() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceAvailability using partial update
        ResourceAvailability partialUpdatedResourceAvailability = new ResourceAvailability();
        partialUpdatedResourceAvailability.setId(resourceAvailability.getId());

        partialUpdatedResourceAvailability.availableFrom(UPDATED_AVAILABLE_FROM).availableTo(UPDATED_AVAILABLE_TO);

        restResourceAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResourceAvailability))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAvailability in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResourceAvailabilityUpdatableFieldsEquals(
            partialUpdatedResourceAvailability,
            getPersistedResourceAvailability(partialUpdatedResourceAvailability)
        );
    }

    @Test
    @Transactional
    void patchNonExistingResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resourceAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(resourceAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(resourceAvailability))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResourceAvailability() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceAvailability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAvailabilityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(resourceAvailability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceAvailability in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResourceAvailability() throws Exception {
        // Initialize the database
        insertedResourceAvailability = resourceAvailabilityRepository.saveAndFlush(resourceAvailability);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the resourceAvailability
        restResourceAvailabilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, resourceAvailability.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return resourceAvailabilityRepository.count();
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

    protected ResourceAvailability getPersistedResourceAvailability(ResourceAvailability resourceAvailability) {
        return resourceAvailabilityRepository.findById(resourceAvailability.getId()).orElseThrow();
    }

    protected void assertPersistedResourceAvailabilityToMatchAllProperties(ResourceAvailability expectedResourceAvailability) {
        assertResourceAvailabilityAllPropertiesEquals(
            expectedResourceAvailability,
            getPersistedResourceAvailability(expectedResourceAvailability)
        );
    }

    protected void assertPersistedResourceAvailabilityToMatchUpdatableProperties(ResourceAvailability expectedResourceAvailability) {
        assertResourceAvailabilityAllUpdatablePropertiesEquals(
            expectedResourceAvailability,
            getPersistedResourceAvailability(expectedResourceAvailability)
        );
    }
}
