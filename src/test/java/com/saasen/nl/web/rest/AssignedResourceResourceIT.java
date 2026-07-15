package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.AssignedResourceAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.AssignedResource;
import com.saasen.nl.repository.AssignedResourceRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssignedResourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssignedResourceResourceIT {

    private static final String ENTITY_API_URL = "/api/assigned-resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignedResourceRepository assignedResourceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignedResourceMockMvc;

    private AssignedResource assignedResource;

    private AssignedResource insertedAssignedResource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignedResource createEntity() {
        return new AssignedResource();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignedResource createUpdatedEntity() {
        return new AssignedResource();
    }

    @BeforeEach
    void initTest() {
        assignedResource = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignedResource != null) {
            assignedResourceRepository.delete(insertedAssignedResource);
            insertedAssignedResource = null;
        }
    }

    @Test
    @Transactional
    void createAssignedResource() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssignedResource
        var returnedAssignedResource = om.readValue(
            restAssignedResourceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignedResource)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignedResource.class
        );

        // Validate the AssignedResource in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAssignedResourceUpdatableFieldsEquals(returnedAssignedResource, getPersistedAssignedResource(returnedAssignedResource));

        insertedAssignedResource = returnedAssignedResource;
    }

    @Test
    @Transactional
    void createAssignedResourceWithExistingId() throws Exception {
        // Create the AssignedResource with an existing ID
        assignedResource.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignedResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignedResource)))
            .andExpect(status().isBadRequest());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssignedResources() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        // Get all the assignedResourceList
        restAssignedResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedResource.getId().intValue())));
    }

    @Test
    @Transactional
    void getAssignedResource() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        // Get the assignedResource
        restAssignedResourceMockMvc
            .perform(get(ENTITY_API_URL_ID, assignedResource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignedResource.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingAssignedResource() throws Exception {
        // Get the assignedResource
        restAssignedResourceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignedResource() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignedResource
        AssignedResource updatedAssignedResource = assignedResourceRepository.findById(assignedResource.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssignedResource are not directly saved in db
        em.detach(updatedAssignedResource);

        restAssignedResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAssignedResource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAssignedResource))
            )
            .andExpect(status().isOk());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignedResourceToMatchAllProperties(updatedAssignedResource);
    }

    @Test
    @Transactional
    void putNonExistingAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignedResource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignedResource))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignedResource))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignedResource)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignedResourceWithPatch() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignedResource using partial update
        AssignedResource partialUpdatedAssignedResource = new AssignedResource();
        partialUpdatedAssignedResource.setId(assignedResource.getId());

        restAssignedResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignedResource.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignedResource))
            )
            .andExpect(status().isOk());

        // Validate the AssignedResource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignedResourceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignedResource, assignedResource),
            getPersistedAssignedResource(assignedResource)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignedResourceWithPatch() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignedResource using partial update
        AssignedResource partialUpdatedAssignedResource = new AssignedResource();
        partialUpdatedAssignedResource.setId(assignedResource.getId());

        restAssignedResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignedResource.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignedResource))
            )
            .andExpect(status().isOk());

        // Validate the AssignedResource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignedResourceUpdatableFieldsEquals(
            partialUpdatedAssignedResource,
            getPersistedAssignedResource(partialUpdatedAssignedResource)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignedResource.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignedResource))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignedResource))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignedResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignedResource.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignedResourceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignedResource)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignedResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignedResource() throws Exception {
        // Initialize the database
        insertedAssignedResource = assignedResourceRepository.saveAndFlush(assignedResource);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignedResource
        restAssignedResourceMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignedResource.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignedResourceRepository.count();
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

    protected AssignedResource getPersistedAssignedResource(AssignedResource assignedResource) {
        return assignedResourceRepository.findById(assignedResource.getId()).orElseThrow();
    }

    protected void assertPersistedAssignedResourceToMatchAllProperties(AssignedResource expectedAssignedResource) {
        assertAssignedResourceAllPropertiesEquals(expectedAssignedResource, getPersistedAssignedResource(expectedAssignedResource));
    }

    protected void assertPersistedAssignedResourceToMatchUpdatableProperties(AssignedResource expectedAssignedResource) {
        assertAssignedResourceAllUpdatablePropertiesEquals(
            expectedAssignedResource,
            getPersistedAssignedResource(expectedAssignedResource)
        );
    }
}
