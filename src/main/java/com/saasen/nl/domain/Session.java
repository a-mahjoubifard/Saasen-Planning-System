package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saasen.nl.domain.enumeration.RequestStatus;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Session.
 */
@Entity
@Table(name = "session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Session implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @JsonIgnoreProperties(value = { "instructor", "session" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private InstructorAvailability instructorAvailability;

    @JsonIgnoreProperties(value = { "location", "session" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private LocationAvailability locationAvailability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "trainingRequest", "course" }, allowSetters = true)
    private RequestedCourse requestedCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    private Freelancer freelancer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Session id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Session startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Session endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public RequestStatus getStatus() {
        return this.status;
    }

    public Session status(RequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public InstructorAvailability getInstructorAvailability() {
        return this.instructorAvailability;
    }

    public void setInstructorAvailability(InstructorAvailability instructorAvailability) {
        this.instructorAvailability = instructorAvailability;
    }

    public Session instructorAvailability(InstructorAvailability instructorAvailability) {
        this.setInstructorAvailability(instructorAvailability);
        return this;
    }

    public LocationAvailability getLocationAvailability() {
        return this.locationAvailability;
    }

    public void setLocationAvailability(LocationAvailability locationAvailability) {
        this.locationAvailability = locationAvailability;
    }

    public Session locationAvailability(LocationAvailability locationAvailability) {
        this.setLocationAvailability(locationAvailability);
        return this;
    }

    public RequestedCourse getRequestedCourse() {
        return this.requestedCourse;
    }

    public void setRequestedCourse(RequestedCourse requestedCourse) {
        this.requestedCourse = requestedCourse;
    }

    public Session requestedCourse(RequestedCourse requestedCourse) {
        this.setRequestedCourse(requestedCourse);
        return this;
    }

    public Freelancer getFreelancer() {
        return this.freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }

    public Session freelancer(Freelancer freelancer) {
        this.setFreelancer(freelancer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
        return getId() != null && getId().equals(((Session) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Session{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
