package com.saasen.nl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saasen.nl.domain.enumeration.FreelancerRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A FreelancerAssignmentRequest.
 */
@Entity
@Table(name = "freelancer_assignment_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FreelancerAssignmentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "requested_at")
    private Instant requestedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FreelancerRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Freelancer freelancer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "instructorAvailability", "locationAvailability", "requestedCourse", "freelancer" },
        allowSetters = true
    )
    private Session session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FreelancerAssignmentRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRequestedAt() {
        return this.requestedAt;
    }

    public FreelancerAssignmentRequest requestedAt(Instant requestedAt) {
        this.setRequestedAt(requestedAt);
        return this;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public FreelancerRequestStatus getStatus() {
        return this.status;
    }

    public FreelancerAssignmentRequest status(FreelancerRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FreelancerRequestStatus status) {
        this.status = status;
    }

    public Freelancer getFreelancer() {
        return this.freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }

    public FreelancerAssignmentRequest freelancer(Freelancer freelancer) {
        this.setFreelancer(freelancer);
        return this;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public FreelancerAssignmentRequest session(Session session) {
        this.setSession(session);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FreelancerAssignmentRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((FreelancerAssignmentRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FreelancerAssignmentRequest{" +
            "id=" + getId() +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
