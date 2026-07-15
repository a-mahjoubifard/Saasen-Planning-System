<template>
  <div>
    <h2 id="page-heading" data-cy="AssignedResourceHeading">
      <span id="assigned-resource">Assigned Resources</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'AssignedResourceCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-assigned-resource"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Assigned Resource</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && assignedResources?.length === 0">
      <span>No Assigned Resources found</span>
    </div>
    <div class="table-responsive" v-if="assignedResources?.length > 0">
      <table class="table table-striped" aria-describedby="assignedResources">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Resource Availability</span></th>
            <th scope="col"><span>Session</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="assignedResource in assignedResources" :key="assignedResource.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AssignedResourceView', params: { assignedResourceId: assignedResource.id } }">{{
                assignedResource.id
              }}</router-link>
            </td>
            <td>
              <div v-if="assignedResource.resourceAvailability">
                <router-link
                  :to="{ name: 'ResourceAvailabilityView', params: { resourceAvailabilityId: assignedResource.resourceAvailability.id } }"
                  >{{ assignedResource.resourceAvailability.id }}</router-link
                >
              </div>
            </td>
            <td>
              <div v-if="assignedResource.session">
                <router-link :to="{ name: 'SessionView', params: { sessionId: assignedResource.session.id } }">{{
                  assignedResource.session.id
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'AssignedResourceView', params: { assignedResourceId: assignedResource.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'AssignedResourceEdit', params: { assignedResourceId: assignedResource.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(assignedResource)"
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
        <span id="saasenApp.assignedResource.delete.question" data-cy="assignedResourceDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-assignedResource-heading">Are you sure you want to delete Assigned Resource {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-assignedResource"
            data-cy="entityConfirmDeleteButton"
            @click="removeAssignedResource"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./assigned-resource.component.ts"></script>
