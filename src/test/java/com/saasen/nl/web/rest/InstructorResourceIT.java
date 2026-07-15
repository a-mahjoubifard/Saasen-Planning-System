package com.saasen.nl.web.rest;

import static com.saasen.nl.domain.InstructorAsserts.*;
import static com.saasen.nl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasen.nl.IntegrationTest;
import com.saasen.nl.domain.Instructor;
import com.saasen.nl.repository.InstructorRepository;
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
 * Integration tests for the {@link InstructorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstructorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_HAS_CAR = false;
    private static final Boolean UPDATED_HAS_CAR = true;

    private static final String ENTITY_API_URL = "/api/instructors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstructorMockMvc;

    private Instructor instructor;

    private Instructor insertedInstructor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instructor createEntity() {
        return new Instructor().name(DEFAULT_NAME).contact(DEFAULT_CONTACT).hasCar(DEFAULT_HAS_CAR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instructor createUpdatedEntity() {
        return new Instructor().name(UPDATED_NAME).contact(UPDATED_CONTACT).hasCar(UPDATED_HAS_CAR);
    }

    @BeforeEach
    void initTest() {
        instructor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInstructor != null) {
            instructorRepository.delete(insertedInstructor);
            insertedInstructor = null;
        }
    }

    @Test
    @Transactional
    void createInstructor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Instructor
        var returnedInstructor = om.readValue(
            restInstructorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructor)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Instructor.class
        );

        // Validate the Instructor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInstructorUpdatableFieldsEquals(returnedInstructor, getPersistedInstructor(returnedInstructor));

        insertedInstructor = returnedInstructor;
    }

    @Test
    @Transactional
    void createInstructorWithExistingId() throws Exception {
        // Create the Instructor with an existing ID
        instructor.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstructorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructor)))
            .andExpect(status().isBadRequest());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instructor.setName(null);

        // Create the Instructor, which fails.

        restInstructorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInstructors() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        // Get all the instructorList
        restInstructorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instructor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].hasCar").value(hasItem(DEFAULT_HAS_CAR)));
    }

    @Test
    @Transactional
    void getInstructor() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        // Get the instructor
        restInstructorMockMvc
            .perform(get(ENTITY_API_URL_ID, instructor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instructor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT))
            .andExpect(jsonPath("$.hasCar").value(DEFAULT_HAS_CAR));
    }

    @Test
    @Transactional
    void getNonExistingInstructor() throws Exception {
        // Get the instructor
        restInstructorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstructor() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor
        Instructor updatedInstructor = instructorRepository.findById(instructor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstructor are not directly saved in db
        em.detach(updatedInstructor);
        updatedInstructor.name(UPDATED_NAME).contact(UPDATED_CONTACT).hasCar(UPDATED_HAS_CAR);

        restInstructorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInstructor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInstructor))
            )
            .andExpect(status().isOk());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstructorToMatchAllProperties(updatedInstructor);
    }

    @Test
    @Transactional
    void putNonExistingInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instructor.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instructor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instructor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstructorWithPatch() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor using partial update
        Instructor partialUpdatedInstructor = new Instructor();
        partialUpdatedInstructor.setId(instructor.getId());

        restInstructorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructor))
            )
            .andExpect(status().isOk());

        // Validate the Instructor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstructor, instructor),
            getPersistedInstructor(instructor)
        );
    }

    @Test
    @Transactional
    void fullUpdateInstructorWithPatch() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor using partial update
        Instructor partialUpdatedInstructor = new Instructor();
        partialUpdatedInstructor.setId(instructor.getId());

        partialUpdatedInstructor.name(UPDATED_NAME).contact(UPDATED_CONTACT).hasCar(UPDATED_HAS_CAR);

        restInstructorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstructor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstructor))
            )
            .andExpect(status().isOk());

        // Validate the Instructor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorUpdatableFieldsEquals(partialUpdatedInstructor, getPersistedInstructor(partialUpdatedInstructor));
    }

    @Test
    @Transactional
    void patchNonExistingInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instructor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instructor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(instructor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstructor() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.saveAndFlush(instructor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instructor
        restInstructorMockMvc
            .perform(delete(ENTITY_API_URL_ID, instructor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return instructorRepository.count();
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

    protected Instructor getPersistedInstructor(Instructor instructor) {
        return instructorRepository.findById(instructor.getId()).orElseThrow();
    }

    protected void assertPersistedInstructorToMatchAllProperties(Instructor expectedInstructor) {
        assertInstructorAllPropertiesEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
    }

    protected void assertPersistedInstructorToMatchUpdatableProperties(Instructor expectedInstructor) {
        assertInstructorAllUpdatablePropertiesEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
    }
}
