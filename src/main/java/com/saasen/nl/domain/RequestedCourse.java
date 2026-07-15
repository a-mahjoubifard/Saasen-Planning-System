package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saasen.nl.domain.enumeration.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A RequestedCourse.
 */
@Entity
@Table(name = "requested_course")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequestedCourse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "number_of_participants", nullable = false)
    private Integer numberOfParticipants;

    @Column(name = "preferred_start_date")
    private LocalDate preferredStartDate;

    @NotNull
    @Column(name = "preferred_location", nullable = false)
    private String preferredLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @Column(name = "actual_start_date")
    private Instant actualStartDate;

    @Column(name = "actual_end_date")
    private Instant actualEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private TrainingRequest trainingRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RequestedCourse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfParticipants() {
        return this.numberOfParticipants;
    }

    public RequestedCourse numberOfParticipants(Integer numberOfParticipants) {
        this.setNumberOfParticipants(numberOfParticipants);
        return this;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public LocalDate getPreferredStartDate() {
        return this.preferredStartDate;
    }

    public RequestedCourse preferredStartDate(LocalDate preferredStartDate) {
        this.setPreferredStartDate(preferredStartDate);
        return this;
    }

    public void setPreferredStartDate(LocalDate preferredStartDate) {
        this.preferredStartDate = preferredStartDate;
    }

    public String getPreferredLocation() {
        return this.preferredLocation;
    }

    public RequestedCourse preferredLocation(String preferredLocation) {
        this.setPreferredLocation(preferredLocation);
        return this;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public RequestStatus getStatus() {
        return this.status;
    }

    public RequestedCourse status(RequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getActualStartDate() {
        return this.actualStartDate;
    }

    public RequestedCourse actualStartDate(Instant actualStartDate) {
        this.setActualStartDate(actualStartDate);
        return this;
    }

    public void setActualStartDate(Instant actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Instant getActualEndDate() {
        return this.actualEndDate;
    }

    public RequestedCourse actualEndDate(Instant actualEndDate) {
        this.setActualEndDate(actualEndDate);
        return this;
    }

    public void setActualEndDate(Instant actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public TrainingRequest getTrainingRequest() {
        return this.trainingRequest;
    }

    public void setTrainingRequest(TrainingRequest trainingRequest) {
        this.trainingRequest = trainingRequest;
    }

    public RequestedCourse trainingRequest(TrainingRequest trainingRequest) {
        this.setTrainingRequest(trainingRequest);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public RequestedCourse course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestedCourse)) {
            return false;
        }
        return getId() != null && getId().equals(((RequestedCourse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequestedCourse{" +
            "id=" + getId() +
            ", numberOfParticipants=" + getNumberOfParticipants() +
            ", preferredStartDate='" + getPreferredStartDate() + "'" +
            ", preferredLocation='" + getPreferredLocation() + "'" +
            ", status='" + getStatus() + "'" +
            ", actualStartDate='" + getActualStartDate() + "'" +
            ", actualEndDate='" + getActualEndDate() + "'" +
            "}";
    }
}
