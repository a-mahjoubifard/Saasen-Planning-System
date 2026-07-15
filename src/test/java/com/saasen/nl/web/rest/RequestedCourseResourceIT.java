package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.RequestedCourseAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.RequestedCourse;
import com.saasen.nl.domain.enumeration.RequestStatus;
import com.saasen.nl.repository.RequestedCourseRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link RequestedCourseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RequestedCourseResourceIT {

    private static final Integer DEFAULT_NUMBER_OF_PARTICIPANTS = 1;
    private static final Integer UPDATED_NUMBER_OF_PARTICIPANTS = 2;

    private static final LocalDate DEFAULT_PREFERRED_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PREFERRED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PREFERRED_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_LOCATION = "BBBBBBBBBB";

    private static final RequestStatus DEFAULT_STATUS = RequestStatus.PENDING;
    private static final RequestStatus UPDATED_STATUS = RequestStatus.PLANNING;

    private static final Instant DEFAULT_ACTUAL_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/requested-courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RequestedCourseRepository requestedCourseRepository;

    @Mock
    private RequestedCourseRepository requestedCourseRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRequestedCourseMockMvc;

    private RequestedCourse requestedCourse;

    private RequestedCourse insertedRequestedCourse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RequestedCourse createEntity() {
        return new RequestedCourse()
            .numberOfParticipants(DEFAULT_NUMBER_OF_PARTICIPANTS)
            .preferredStartDate(DEFAULT_PREFERRED_START_DATE)
            .preferredLocation(DEFAULT_PREFERRED_LOCATION)
            .status(DEFAULT_STATUS)
            .actualStartDate(DEFAULT_ACTUAL_START_DATE)
            .actualEndDate(DEFAULT_ACTUAL_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RequestedCourse createUpdatedEntity() {
        return new RequestedCourse()
            .numberOfParticipants(UPDATED_NUMBER_OF_PARTICIPANTS)
            .preferredStartDate(UPDATED_PREFERRED_START_DATE)
            .preferredLocation(UPDATED_PREFERRED_LOCATION)
            .status(UPDATED_STATUS)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE);
    }

    @BeforeEach
    void initTest() {
        requestedCourse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRequestedCourse != null) {
            requestedCourseRepository.delete(insertedRequestedCourse);
            insertedRequestedCourse = null;
        }
    }

    @Test
    @Transactional
    void createRequestedCourse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RequestedCourse
        var returnedRequestedCourse = om.readValue(
            restRequestedCourseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestedCourse)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RequestedCourse.class
        );

        // Validate the RequestedCourse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRequestedCourseUpdatableFieldsEquals(returnedRequestedCourse, getPersistedRequestedCourse(returnedRequestedCourse));

        insertedRequestedCourse = returnedRequestedCourse;
    }

    @Test
    @Transactional
    void createRequestedCourseWithExistingId() throws Exception {
        // Create the RequestedCourse with an existing ID
        requestedCourse.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRequestedCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestedCourse)))
            .andExpect(status().isBadRequest());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumberOfParticipantsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        requestedCourse.setNumberOfParticipants(null);

        // Create the RequestedCourse, which fails.

        restRequestedCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestedCourse)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPreferredLocationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        requestedCourse.setPreferredLocation(null);

        // Create the RequestedCourse, which fails.

        restRequestedCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestedCourse)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRequestedCourses() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        // Get all the requestedCourseList
        restRequestedCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestedCourse.getId().intValue())))
            .andExpect(jsonPath("$.[*].numberOfParticipants").value(hasItem(DEFAULT_NUMBER_OF_PARTICIPANTS)))
            .andExpect(jsonPath("$.[*].preferredStartDate").value(hasItem(DEFAULT_PREFERRED_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].preferredLocation").value(hasItem(DEFAULT_PREFERRED_LOCATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].actualStartDate").value(hasItem(DEFAULT_ACTUAL_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualEndDate").value(hasItem(DEFAULT_ACTUAL_END_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestedCoursesWithEagerRelationshipsIsEnabled() throws Exception {
        when(requestedCourseRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestedCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(requestedCourseRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestedCoursesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(requestedCourseRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestedCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(requestedCourseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRequestedCourse() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        // Get the requestedCourse
        restRequestedCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, requestedCourse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(requestedCourse.getId().intValue()))
            .andExpect(jsonPath("$.numberOfParticipants").value(DEFAULT_NUMBER_OF_PARTICIPANTS))
            .andExpect(jsonPath("$.preferredStartDate").value(DEFAULT_PREFERRED_START_DATE.toString()))
            .andExpect(jsonPath("$.preferredLocation").value(DEFAULT_PREFERRED_LOCATION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.actualStartDate").value(DEFAULT_ACTUAL_START_DATE.toString()))
            .andExpect(jsonPath("$.actualEndDate").value(DEFAULT_ACTUAL_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRequestedCourse() throws Exception {
        // Get the requestedCourse
        restRequestedCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRequestedCourse() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestedCourse
        RequestedCourse updatedRequestedCourse = requestedCourseRepository.findById(requestedCourse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRequestedCourse are not directly saved in db
        em.detach(updatedRequestedCourse);
        updatedRequestedCourse
            .numberOfParticipants(UPDATED_NUMBER_OF_PARTICIPANTS)
            .preferredStartDate(UPDATED_PREFERRED_START_DATE)
            .preferredLocation(UPDATED_PREFERRED_LOCATION)
            .status(UPDATED_STATUS)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE);

        restRequestedCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRequestedCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRequestedCourse))
            )
            .andExpect(status().isOk());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRequestedCourseToMatchAllProperties(updatedRequestedCourse);
    }

    @Test
    @Transactional
    void putNonExistingRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, requestedCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestedCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestedCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestedCourse)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRequestedCourseWithPatch() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestedCourse using partial update
        RequestedCourse partialUpdatedRequestedCourse = new RequestedCourse();
        partialUpdatedRequestedCourse.setId(requestedCourse.getId());

        partialUpdatedRequestedCourse.preferredStartDate(UPDATED_PREFERRED_START_DATE).status(UPDATED_STATUS);

        restRequestedCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequestedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequestedCourse))
            )
            .andExpect(status().isOk());

        // Validate the RequestedCourse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestedCourseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRequestedCourse, requestedCourse),
            getPersistedRequestedCourse(requestedCourse)
        );
    }

    @Test
    @Transactional
    void fullUpdateRequestedCourseWithPatch() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestedCourse using partial update
        RequestedCourse partialUpdatedRequestedCourse = new RequestedCourse();
        partialUpdatedRequestedCourse.setId(requestedCourse.getId());

        partialUpdatedRequestedCourse
            .numberOfParticipants(UPDATED_NUMBER_OF_PARTICIPANTS)
            .preferredStartDate(UPDATED_PREFERRED_START_DATE)
            .preferredLocation(UPDATED_PREFERRED_LOCATION)
            .status(UPDATED_STATUS)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE);

        restRequestedCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequestedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequestedCourse))
            )
            .andExpect(status().isOk());

        // Validate the RequestedCourse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestedCourseUpdatableFieldsEquals(
            partialUpdatedRequestedCourse,
            getPersistedRequestedCourse(partialUpdatedRequestedCourse)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, requestedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestedCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestedCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRequestedCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestedCourse.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestedCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(requestedCourse)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RequestedCourse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRequestedCourse() throws Exception {
        // Initialize the database
        insertedRequestedCourse = requestedCourseRepository.saveAndFlush(requestedCourse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the requestedCourse
        restRequestedCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, requestedCourse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return requestedCourseRepository.count();
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

    protected RequestedCourse getPersistedRequestedCourse(RequestedCourse requestedCourse) {
        return requestedCourseRepository.findById(requestedCourse.getId()).orElseThrow();
    }

    protected void assertPersistedRequestedCourseToMatchAllProperties(RequestedCourse expectedRequestedCourse) {
        assertRequestedCourseAllPropertiesEquals(expectedRequestedCourse, getPersistedRequestedCourse(expectedRequestedCourse));
    }

    protected void assertPersistedRequestedCourseToMatchUpdatableProperties(RequestedCourse expectedRequestedCourse) {
        assertRequestedCourseAllUpdatablePropertiesEquals(expectedRequestedCourse, getPersistedRequestedCourse(expectedRequestedCourse));
    }
}
