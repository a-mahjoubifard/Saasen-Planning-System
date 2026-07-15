<template>
  <div>
    <h2 id="page-heading" data-cy="LocationAvailabilityHeading">
      <span id="location-availability">Location Availabilities</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'LocationAvailabilityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-location-availability"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Location Availability</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && locationAvailabilities?.length === 0">
      <span>No Location Availabilities found</span>
    </div>
    <div class="table-responsive" v-if="locationAvailabilities?.length > 0">
      <table class="table table-striped" aria-describedby="locationAvailabilities">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Title</span></th>
            <th scope="col"><span>Available From</span></th>
            <th scope="col"><span>Available To</span></th>
            <th scope="col"><span>Location</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="locationAvailability in locationAvailabilities" :key="locationAvailability.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'LocationAvailabilityView', params: { locationAvailabilityId: locationAvailability.id } }">{{
                locationAvailability.id
              }}</router-link>
            </td>
            <td>{{ locationAvailability.title }}</td>
            <td>{{ formatDateShort(locationAvailability.availableFrom) || '' }}</td>
            <td>{{ formatDateShort(locationAvailability.availableTo) || '' }}</td>
            <td>
              <div v-if="locationAvailability.location">
                <router-link :to="{ name: 'LocationView', params: { locationId: locationAvailability.location.id } }">{{
                  locationAvailability.location.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'LocationAvailabilityView', params: { locationAvailabilityId: locationAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'LocationAvailabilityEdit', params: { locationAvailabilityId: locationAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(locationAvailability)"
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
        <span id="saasenApp.locationAvailability.delete.question" data-cy="locationAvailabilityDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-locationAvailability-heading">Are you sure you want to delete Location Availability {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-locationAvailability"
            data-cy="entityConfirmDeleteButton"
            @click="removeLocationAvailability"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./location-availability.component.ts"></script>
