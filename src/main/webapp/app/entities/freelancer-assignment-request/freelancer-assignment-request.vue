<template>
  <div>
    <h2 id="page-heading" data-cy="FreelancerAssignmentRequestHeading">
      <span id="freelancer-assignment-request">Freelancer Assignment Requests</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'FreelancerAssignmentRequestCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-freelancer-assignment-request"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Freelancer Assignment Request</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && freelancerAssignmentRequests?.length === 0">
      <span>No Freelancer Assignment Requests found</span>
    </div>
    <div class="table-responsive" v-if="freelancerAssignmentRequests?.length > 0">
      <table class="table table-striped" aria-describedby="freelancerAssignmentRequests">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Requested At</span></th>
            <th scope="col"><span>Status</span></th>
            <th scope="col"><span>Freelancer</span></th>
            <th scope="col"><span>Session</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="freelancerAssignmentRequest in freelancerAssignmentRequests"
            :key="freelancerAssignmentRequest.id"
            data-cy="entityTable"
          >
            <td>
              <router-link
                :to="{ name: 'FreelancerAssignmentRequestView', params: { freelancerAssignmentRequestId: freelancerAssignmentRequest.id } }"
                >{{ freelancerAssignmentRequest.id }}</router-link
              >
            </td>
            <td>{{ formatDateShort(freelancerAssignmentRequest.requestedAt) || '' }}</td>
            <td>{{ freelancerAssignmentRequest.status }}</td>
            <td>
              <div v-if="freelancerAssignmentRequest.freelancer">
                <router-link :to="{ name: 'FreelancerView', params: { freelancerId: freelancerAssignmentRequest.freelancer.id } }">{{
                  freelancerAssignmentRequest.freelancer.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="freelancerAssignmentRequest.session">
                <router-link :to="{ name: 'SessionView', params: { sessionId: freelancerAssignmentRequest.session.id } }">{{
                  freelancerAssignmentRequest.session.id
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{
                    name: 'FreelancerAssignmentRequestView',
                    params: { freelancerAssignmentRequestId: freelancerAssignmentRequest.id },
                  }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{
                    name: 'FreelancerAssignmentRequestEdit',
                    params: { freelancerAssignmentRequestId: freelancerAssignmentRequest.id },
                  }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(freelancerAssignmentRequest)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #title>
        <span id="saasenApp.freelancerAssignmentRequest.delete.question" data-cy="freelancerAssignmentRequestDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-freelancerAssignmentRequest-heading">
          Are you sure you want to delete Freelancer Assignment Request {{ removeId }}?
        </p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-freelancerAssignmentRequest"
            data-cy="entityConfirmDeleteButton"
            @click="removeFreelancerAssignmentRequest"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./freelancer-assignment-request.component.ts"></script>
