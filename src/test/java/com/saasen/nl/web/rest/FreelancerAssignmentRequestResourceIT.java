package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.FreelancerAssignmentRequestAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.FreelancerAssignmentRequest;
import com.saasen.nl.domain.enumeration.FreelancerRequestStatus;
import com.saasen.nl.repository.FreelancerAssignmentRequestRepository;
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
 * Integration tests for the {@link FreelancerAssignmentRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FreelancerAssignmentRequestResourceIT {

    private static final Instant DEFAULT_REQUESTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final FreelancerRequestStatus DEFAULT_STATUS = FreelancerRequestStatus.REQUESTEDFOR;
    private static final FreelancerRequestStatus UPDATED_STATUS = FreelancerRequestStatus.APPROVED;

    private static final String ENTITY_API_URL = "/api/freelancer-assignment-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FreelancerAssignmentRequestRepository freelancerAssignmentRequestRepository;

    @Mock
    private FreelancerAssignmentRequestRepository freelancerAssignmentRequestRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFreelancerAssignmentRequestMockMvc;

    private FreelancerAssignmentRequest freelancerAssignmentRequest;

    private FreelancerAssignmentRequest insertedFreelancerAssignmentRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FreelancerAssignmentRequest createEntity() {
        return new FreelancerAssignmentRequest().requestedAt(DEFAULT_REQUESTED_AT).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FreelancerAssignmentRequest createUpdatedEntity() {
        return new FreelancerAssignmentRequest().requestedAt(UPDATED_REQUESTED_AT).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        freelancerAssignmentRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFreelancerAssignmentRequest != null) {
            freelancerAssignmentRequestRepository.delete(insertedFreelancerAssignmentRequest);
            insertedFreelancerAssignmentRequest = null;
        }
    }

    @Test
    @Transactional
    void createFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FreelancerAssignmentRequest
        var returnedFreelancerAssignmentRequest = om.readValue(
            restFreelancerAssignmentRequestMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancerAssignmentRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FreelancerAssignmentRequest.class
        );

        // Validate the FreelancerAssignmentRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFreelancerAssignmentRequestUpdatableFieldsEquals(
            returnedFreelancerAssignmentRequest,
            getPersistedFreelancerAssignmentRequest(returnedFreelancerAssignmentRequest)
        );

        insertedFreelancerAssignmentRequest = returnedFreelancerAssignmentRequest;
    }

    @Test
    @Transactional
    void createFreelancerAssignmentRequestWithExistingId() throws Exception {
        // Create the FreelancerAssignmentRequest with an existing ID
        freelancerAssignmentRequest.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFreelancerAssignmentRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        freelancerAssignmentRequest.setStatus(null);

        // Create the FreelancerAssignmentRequest, which fails.

        restFreelancerAssignmentRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFreelancerAssignmentRequests() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        // Get all the freelancerAssignmentRequestList
        restFreelancerAssignmentRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(freelancerAssignmentRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestedAt").value(hasItem(DEFAULT_REQUESTED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFreelancerAssignmentRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(freelancerAssignmentRequestRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFreelancerAssignmentRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(freelancerAssignmentRequestRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFreelancerAssignmentRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(freelancerAssignmentRequestRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFreelancerAssignmentRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(freelancerAssignmentRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFreelancerAssignmentRequest() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        // Get the freelancerAssignmentRequest
        restFreelancerAssignmentRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, freelancerAssignmentRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(freelancerAssignmentRequest.getId().intValue()))
            .andExpect(jsonPath("$.requestedAt").value(DEFAULT_REQUESTED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFreelancerAssignmentRequest() throws Exception {
        // Get the freelancerAssignmentRequest
        restFreelancerAssignmentRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFreelancerAssignmentRequest() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancerAssignmentRequest
        FreelancerAssignmentRequest updatedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository
            .findById(freelancerAssignmentRequest.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedFreelancerAssignmentRequest are not directly saved in db
        em.detach(updatedFreelancerAssignmentRequest);
        updatedFreelancerAssignmentRequest.requestedAt(UPDATED_REQUESTED_AT).status(UPDATED_STATUS);

        restFreelancerAssignmentRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFreelancerAssignmentRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFreelancerAssignmentRequest))
            )
            .andExpect(status().isOk());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFreelancerAssignmentRequestToMatchAllProperties(updatedFreelancerAssignmentRequest);
    }

    @Test
    @Transactional
    void putNonExistingFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, freelancerAssignmentRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(freelancerAssignmentRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFreelancerAssignmentRequestWithPatch() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancerAssignmentRequest using partial update
        FreelancerAssignmentRequest partialUpdatedFreelancerAssignmentRequest = new FreelancerAssignmentRequest();
        partialUpdatedFreelancerAssignmentRequest.setId(freelancerAssignmentRequest.getId());

        restFreelancerAssignmentRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFreelancerAssignmentRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFreelancerAssignmentRequest))
            )
            .andExpect(status().isOk());

        // Validate the FreelancerAssignmentRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFreelancerAssignmentRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFreelancerAssignmentRequest, freelancerAssignmentRequest),
            getPersistedFreelancerAssignmentRequest(freelancerAssignmentRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateFreelancerAssignmentRequestWithPatch() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the freelancerAssignmentRequest using partial update
        FreelancerAssignmentRequest partialUpdatedFreelancerAssignmentRequest = new FreelancerAssignmentRequest();
        partialUpdatedFreelancerAssignmentRequest.setId(freelancerAssignmentRequest.getId());

        partialUpdatedFreelancerAssignmentRequest.requestedAt(UPDATED_REQUESTED_AT).status(UPDATED_STATUS);

        restFreelancerAssignmentRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFreelancerAssignmentRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFreelancerAssignmentRequest))
            )
            .andExpect(status().isOk());

        // Validate the FreelancerAssignmentRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFreelancerAssignmentRequestUpdatableFieldsEquals(
            partialUpdatedFreelancerAssignmentRequest,
            getPersistedFreelancerAssignmentRequest(partialUpdatedFreelancerAssignmentRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, freelancerAssignmentRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFreelancerAssignmentRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        freelancerAssignmentRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFreelancerAssignmentRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(freelancerAssignmentRequest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FreelancerAssignmentRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFreelancerAssignmentRequest() throws Exception {
        // Initialize the database
        insertedFreelancerAssignmentRequest = freelancerAssignmentRequestRepository.saveAndFlush(freelancerAssignmentRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the freelancerAssignmentRequest
        restFreelancerAssignmentRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, freelancerAssignmentRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return freelancerAssignmentRequestRepository.count();
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

    protected FreelancerAssignmentRequest getPersistedFreelancerAssignmentRequest(FreelancerAssignmentRequest freelancerAssignmentRequest) {
        return freelancerAssignmentRequestRepository.findById(freelancerAssignmentRequest.getId()).orElseThrow();
    }

    protected void assertPersistedFreelancerAssignmentRequestToMatchAllProperties(
        FreelancerAssignmentRequest expectedFreelancerAssignmentRequest
    ) {
        assertFreelancerAssignmentRequestAllPropertiesEquals(
            expectedFreelancerAssignmentRequest,
            getPersistedFreelancerAssignmentRequest(expectedFreelancerAssignmentRequest)
        );
    }

    protected void assertPersistedFreelancerAssignmentRequestToMatchUpdatableProperties(
        FreelancerAssignmentRequest expectedFreelancerAssignmentRequest
    ) {
        assertFreelancerAssignmentRequestAllUpdatablePropertiesEquals(
            expectedFreelancerAssignmentRequest,
            getPersistedFreelancerAssignmentRequest(expectedFreelancerAssignmentRequest)
        );
    }
}
