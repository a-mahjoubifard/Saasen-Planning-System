<template>
  <div>
    <h2 id="page-heading" data-cy="LocationHeading">
      <span id="location">Locations</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'LocationCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-location"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Location</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && locations?.length === 0">
      <span>No Locations found</span>
    </div>
    <div class="table-responsive" v-if="locations?.length > 0">
      <table class="table table-striped" aria-describedby="locations">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Name</span></th>
            <th scope="col"><span>Address</span></th>
            <th scope="col"><span>Capacity</span></th>
            <th scope="col"><span>Equipment Type</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="location in locations" :key="location.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'LocationView', params: { locationId: location.id } }">{{ location.id }}</router-link>
            </td>
            <td>{{ location.name }}</td>
            <td>{{ location.address }}</td>
            <td>{{ location.capacity }}</td>
            <td>{{ location.equipmentType }}</td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'LocationView', params: { locationId: location.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'LocationEdit', params: { locationId: location.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(location)"
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
        <span id="saasenApp.location.delete.question" data-cy="locationDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-location-heading">Are you sure you want to delete Location {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-location"
            data-cy="entityConfirmDeleteButton"
            @click="removeLocation"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./location.component.ts"></script>
