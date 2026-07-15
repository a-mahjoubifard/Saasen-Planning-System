<template>
  <div>
    <h2 id="page-heading" data-cy="TrainingRequestHeading">
      <span id="training-request">Training Requests</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'TrainingRequestCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-training-request"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Training Request</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && trainingRequests?.length === 0">
      <span>No Training Requests found</span>
    </div>
    <div class="table-responsive" v-if="trainingRequests?.length > 0">
      <table class="table table-striped" aria-describedby="trainingRequests">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Request Date</span></th>
            <th scope="col"><span>Description</span></th>
            <th scope="col"><span>Customer</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trainingRequest in trainingRequests" :key="trainingRequest.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrainingRequestView', params: { trainingRequestId: trainingRequest.id } }">{{
                trainingRequest.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(trainingRequest.requestDate) || '' }}</td>
            <td>{{ trainingRequest.description }}</td>
            <td>
              <div v-if="trainingRequest.customer">
                <router-link :to="{ name: 'CustomerView', params: { customerId: trainingRequest.customer.id } }">{{
                  trainingRequest.customer.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'TrainingRequestView', params: { trainingRequestId: trainingRequest.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'TrainingRequestEdit', params: { trainingRequestId: trainingRequest.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(trainingRequest)"
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
        <span id="saasenApp.trainingRequest.delete.question" data-cy="trainingRequestDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trainingRequest-heading">Are you sure you want to delete Training Request {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trainingRequest"
            data-cy="entityConfirmDeleteButton"
            @click="removeTrainingRequest"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./training-request.component.ts"></script>
