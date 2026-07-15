<template>
  <div>
    <h2 id="page-heading" data-cy="ResourceAvailabilityHeading">
      <span id="resource-availability">Resource Availabilities</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ResourceAvailabilityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-resource-availability"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Resource Availability</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && resourceAvailabilities?.length === 0">
      <span>No Resource Availabilities found</span>
    </div>
    <div class="table-responsive" v-if="resourceAvailabilities?.length > 0">
      <table class="table table-striped" aria-describedby="resourceAvailabilities">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Available From</span></th>
            <th scope="col"><span>Available To</span></th>
            <th scope="col"><span>Resource</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="resourceAvailability in resourceAvailabilities" :key="resourceAvailability.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ResourceAvailabilityView', params: { resourceAvailabilityId: resourceAvailability.id } }">{{
                resourceAvailability.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(resourceAvailability.availableFrom) || '' }}</td>
            <td>{{ formatDateShort(resourceAvailability.availableTo) || '' }}</td>
            <td>
              <div v-if="resourceAvailability.resource">
                <router-link :to="{ name: 'ResourceView', params: { resourceId: resourceAvailability.resource.id } }">{{
                  resourceAvailability.resource.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ResourceAvailabilityView', params: { resourceAvailabilityId: resourceAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ResourceAvailabilityEdit', params: { resourceAvailabilityId: resourceAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(resourceAvailability)"
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
        <span id="saasenApp.resourceAvailability.delete.question" data-cy="resourceAvailabilityDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-resourceAvailability-heading">Are you sure you want to delete Resource Availability {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-resourceAvailability"
            data-cy="entityConfirmDeleteButton"
            @click="removeResourceAvailability"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./resource-availability.component.ts"></script>
