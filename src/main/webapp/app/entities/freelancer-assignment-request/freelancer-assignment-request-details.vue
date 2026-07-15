<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="freelancerAssignmentRequest">
        <h2 class="jh-entity-heading" data-cy="freelancerAssignmentRequestDetailsHeading">
          <span>Freelancer Assignment RequestFreelancerAssignmentRequest</span> {{ freelancerAssignmentRequest.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Requested At</span>
          </dt>
          <dd>
            <span v-if="freelancerAssignmentRequest.requestedAt">{{ formatDateLong(freelancerAssignmentRequest.requestedAt) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ freelancerAssignmentRequest.status }}</span>
          </dd>
          <dt>
            <span>Freelancer</span>
          </dt>
          <dd>
            <div v-if="freelancerAssignmentRequest.freelancer">
              <router-link :to="{ name: 'FreelancerView', params: { freelancerId: freelancerAssignmentRequest.freelancer.id } }">{{
                freelancerAssignmentRequest.freelancer.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Session</span>
          </dt>
          <dd>
            <div v-if="freelancerAssignmentRequest.session">
              <router-link :to="{ name: 'SessionView', params: { sessionId: freelancerAssignmentRequest.session.id } }">{{
                freelancerAssignmentRequest.session.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="freelancerAssignmentRequest.id"
          :to="{ name: 'FreelancerAssignmentRequestEdit', params: { freelancerAssignmentRequestId: freelancerAssignmentRequest.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./freelancer-assignment-request-details.component.ts"></script>
