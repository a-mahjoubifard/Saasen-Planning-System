<template>
  <div>
    <h2 id="page-heading" data-cy="InstructorAvailabilityHeading">
      <span id="instructor-availability">Instructor Availabilities</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InstructorAvailabilityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-instructor-availability"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Instructor Availability</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && instructorAvailabilities?.length === 0">
      <span>No Instructor Availabilities found</span>
    </div>
    <div class="table-responsive" v-if="instructorAvailabilities?.length > 0">
      <table class="table table-striped" aria-describedby="instructorAvailabilities">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Title</span></th>
            <th scope="col"><span>Available From</span></th>
            <th scope="col"><span>Available To</span></th>
            <th scope="col"><span>Instructor</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="instructorAvailability in instructorAvailabilities" :key="instructorAvailability.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InstructorAvailabilityView', params: { instructorAvailabilityId: instructorAvailability.id } }">{{
                instructorAvailability.id
              }}</router-link>
            </td>
            <td>{{ instructorAvailability.title }}</td>
            <td>{{ formatDateShort(instructorAvailability.availableFrom) || '' }}</td>
            <td>{{ formatDateShort(instructorAvailability.availableTo) || '' }}</td>
            <td>
              <div v-if="instructorAvailability.instructor">
                <router-link :to="{ name: 'InstructorView', params: { instructorId: instructorAvailability.instructor.id } }">{{
                  instructorAvailability.instructor.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InstructorAvailabilityView', params: { instructorAvailabilityId: instructorAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InstructorAvailabilityEdit', params: { instructorAvailabilityId: instructorAvailability.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(instructorAvailability)"
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
        <span id="saasenApp.instructorAvailability.delete.question" data-cy="instructorAvailabilityDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-instructorAvailability-heading">Are you sure you want to delete Instructor Availability {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-instructorAvailability"
            data-cy="entityConfirmDeleteButton"
            @click="removeInstructorAvailability"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./instructor-availability.component.ts"></script>
