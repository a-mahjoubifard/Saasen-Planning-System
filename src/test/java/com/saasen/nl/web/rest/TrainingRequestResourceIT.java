package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.TrainingRequestAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.TrainingRequest;
import com.saasen.nl.repository.TrainingRequestRepository;
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
 * Integration tests for the {@link TrainingRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TrainingRequestResourceIT {

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/training-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrainingRequestRepository trainingRequestRepository;

    @Mock
    private TrainingRequestRepository trainingRequestRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTrainingRequestMockMvc;

    private TrainingRequest trainingRequest;

    private TrainingRequest insertedTrainingRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrainingRequest createEntity() {
        return new TrainingRequest().requestDate(DEFAULT_REQUEST_DATE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrainingRequest createUpdatedEntity() {
        return new TrainingRequest().requestDate(UPDATED_REQUEST_DATE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        trainingRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTrainingRequest != null) {
            trainingRequestRepository.delete(insertedTrainingRequest);
            insertedTrainingRequest = null;
        }
    }

    @Test
    @Transactional
    void createTrainingRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TrainingRequest
        var returnedTrainingRequest = om.readValue(
            restTrainingRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trainingRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TrainingRequest.class
        );

        // Validate the TrainingRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTrainingRequestUpdatableFieldsEquals(returnedTrainingRequest, getPersistedTrainingRequest(returnedTrainingRequest));

        insertedTrainingRequest = returnedTrainingRequest;
    }

    @Test
    @Transactional
    void createTrainingRequestWithExistingId() throws Exception {
        // Create the TrainingRequest with an existing ID
        trainingRequest.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrainingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trainingRequest)))
            .andExpect(status().isBadRequest());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trainingRequest.setDescription(null);

        // Create the TrainingRequest, which fails.

        restTrainingRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trainingRequest)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTrainingRequests() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        // Get all the trainingRequestList
        restTrainingRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trainingRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrainingRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(trainingRequestRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTrainingRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(trainingRequestRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrainingRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(trainingRequestRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTrainingRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(trainingRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTrainingRequest() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        // Get the trainingRequest
        restTrainingRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, trainingRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trainingRequest.getId().intValue()))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingTrainingRequest() throws Exception {
        // Get the trainingRequest
        restTrainingRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrainingRequest() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trainingRequest
        TrainingRequest updatedTrainingRequest = trainingRequestRepository.findById(trainingRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrainingRequest are not directly saved in db
        em.detach(updatedTrainingRequest);
        updatedTrainingRequest.requestDate(UPDATED_REQUEST_DATE).description(UPDATED_DESCRIPTION);

        restTrainingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTrainingRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTrainingRequest))
            )
            .andExpect(status().isOk());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrainingRequestToMatchAllProperties(updatedTrainingRequest);
    }

    @Test
    @Transactional
    void putNonExistingTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trainingRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trainingRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trainingRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trainingRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTrainingRequestWithPatch() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trainingRequest using partial update
        TrainingRequest partialUpdatedTrainingRequest = new TrainingRequest();
        partialUpdatedTrainingRequest.setId(trainingRequest.getId());

        partialUpdatedTrainingRequest.description(UPDATED_DESCRIPTION);

        restTrainingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrainingRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrainingRequest))
            )
            .andExpect(status().isOk());

        // Validate the TrainingRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrainingRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrainingRequest, trainingRequest),
            getPersistedTrainingRequest(trainingRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateTrainingRequestWithPatch() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trainingRequest using partial update
        TrainingRequest partialUpdatedTrainingRequest = new TrainingRequest();
        partialUpdatedTrainingRequest.setId(trainingRequest.getId());

        partialUpdatedTrainingRequest.requestDate(UPDATED_REQUEST_DATE).description(UPDATED_DESCRIPTION);

        restTrainingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrainingRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrainingRequest))
            )
            .andExpect(status().isOk());

        // Validate the TrainingRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrainingRequestUpdatableFieldsEquals(
            partialUpdatedTrainingRequest,
            getPersistedTrainingRequest(partialUpdatedTrainingRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, trainingRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trainingRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trainingRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrainingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trainingRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrainingRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trainingRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TrainingRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrainingRequest() throws Exception {
        // Initialize the database
        insertedTrainingRequest = trainingRequestRepository.saveAndFlush(trainingRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trainingRequest
        restTrainingRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, trainingRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return trainingRequestRepository.count();
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

    protected TrainingRequest getPersistedTrainingRequest(TrainingRequest trainingRequest) {
        return trainingRequestRepository.findById(trainingRequest.getId()).orElseThrow();
    }

    protected void assertPersistedTrainingRequestToMatchAllProperties(TrainingRequest expectedTrainingRequest) {
        assertTrainingRequestAllPropertiesEquals(expectedTrainingRequest, getPersistedTrainingRequest(expectedTrainingRequest));
    }

    protected void assertPersistedTrainingRequestToMatchUpdatableProperties(TrainingRequest expectedTrainingRequest) {
        assertTrainingRequestAllUpdatablePropertiesEquals(expectedTrainingRequest, getPersistedTrainingRequest(expectedTrainingRequest));
    }
}
