package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.FreelancerAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.Freelancer;
import com.saasen.nl.repository.FreelancerRepository;
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
 * Integration tests for the {@link FreelancerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FreelancerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String DEFAULT_QUALIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_QUALIFICATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/freelancers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFreelancerMockMvc;

    private Freelancer freelancer;

    private Freelancer insertedFreelancer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Freelancer createEntity() {
        return new Freelancer().name(DEFAULT_NAME).contact(DEFAULT_CONTACT).qualification(DEFAULT_QUALIFICATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Freelancer createUpdatedEntity() {
        return new Freelancer().name(UPDATED_NAME).contact(UPDATED_CONTACT).qualification(UPDATED_QUALIFICATION);
    }

    @BeforeEach
    void initTest() {
        freelancer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFreelancer != null) {
            freelancerRepository.delete(insertedFreelancer);
            insertedFreelancer = null;
        }
    }

    @Test
    @Transactional
    void createFreelancer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Freelancer
        var returnedFreelancer = om.readValue(
            restFreelancerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Freelancer.class
        );

        // Validate the Freelancer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFreelancerUpdatableFieldsEquals(returnedFreelancer, getPersistedFreelancer(returnedFreelancer));

        insertedFreelancer = returnedFreelancer;
    }

    @Test
    @Transactional
    void createFreelancerWithExistingId() throws Exception {
        // Create the Freelancer with an existing ID
        freelancer.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFreelancerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancer)))
            .andExpect(status().isBadRequest());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        freelancer.setName(null);

        // Create the Freelancer, which fails.

        restFreelancerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancer)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFreelancers() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        // Get all the freelancerList
        restFreelancerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(freelancer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].qualification").value(hasItem(DEFAULT_QUALIFICATION)));
    }

    @Test
    @Transactional
    void getFreelancer() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        // Get the freelancer
        restFreelancerMockMvc
            .perform(get(ENTITY_API_URL_ID, freelancer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(freelancer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT))
            .andExpect(jsonPath("$.qualification").value(DEFAULT_QUALIFICATION));
    }

    @Test
    @Transactional
    void getNonExistingFreelancer() throws Exception {
        // Get the freelancer
        restFreelancerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFreelancer() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancer
        Freelancer updatedFreelancer = freelancerRepository.findById(freelancer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFreelancer are not directly saved in db
        em.detach(updatedFreelancer);
        updatedFreelancer.name(UPDATED_NAME).contact(UPDATED_CONTACT).qualification(UPDATED_QUALIFICATION);

        restFreelancerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFreelancer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFreelancer))
            )
            .andExpect(status().isOk());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFreelancerToMatchAllProperties(updatedFreelancer);
    }

    @Test
    @Transactional
    void putNonExistingFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, freelancer.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(freelancer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFreelancerWithPatch() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancer using partial update
        Freelancer partialUpdatedFreelancer = new Freelancer();
        partialUpdatedFreelancer.setId(freelancer.getId());

        partialUpdatedFreelancer.qualification(UPDATED_QUALIFICATION);

        restFreelancerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFreelancer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFreelancer))
            )
            .andExpect(status().isOk());

        // Validate the Freelancer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFreelancerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFreelancer, freelancer),
            getPersistedFreelancer(freelancer)
        );
    }

    @Test
    @Transactional
    void fullUpdateFreelancerWithPatch() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancer using partial update
        Freelancer partialUpdatedFreelancer = new Freelancer();
        partialUpdatedFreelancer.setId(freelancer.getId());

        partialUpdatedFreelancer.name(UPDATED_NAME).contact(UPDATED_CONTACT).qualification(UPDATED_QUALIFICATION);

        restFreelancerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFreelancer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFreelancer))
            )
            .andExpect(status().isOk());

        // Validate the Freelancer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFreelancerUpdatableFieldsEquals(partialUpdatedFreelancer, getPersistedFreelancer(partialUpdatedFreelancer));
    }

    @Test
    @Transactional
    void patchNonExistingFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, freelancer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(freelancer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(freelancer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFreelancer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(freelancer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Freelancer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFreelancer() throws Exception {
        // Initialize the database
        insertedFreelancer = freelancerRepository.saveAndFlush(freelancer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the freelancer
        restFreelancerMockMvc
            .perform(delete(ENTITY_API_URL_ID, freelancer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return freelancerRepository.count();
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

    protected Freelancer getPersistedFreelancer(Freelancer freelancer) {
        return freelancerRepository.findById(freelancer.getId()).orElseThrow();
    }

    protected void assertPersistedFreelancerToMatchAllProperties(Freelancer expectedFreelancer) {
        assertFreelancerAllPropertiesEquals(expectedFreelancer, getPersistedFreelancer(expectedFreelancer));
    }

    protected void assertPersistedFreelancerToMatchUpdatableProperties(Freelancer expectedFreelancer) {
        assertFreelancerAllUpdatablePropertiesEquals(expectedFreelancer, getPersistedFreelancer(expectedFreelancer));
    }
}
