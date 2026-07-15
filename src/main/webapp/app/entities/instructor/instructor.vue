<template>
  <div>
    <h2 id="page-heading" data-cy="InstructorHeading">
      <span id="instructor">Instructors</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InstructorCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-instructor"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Instructor</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && instructors?.length === 0">
      <span>No Instructors found</span>
    </div>
    <div class="table-responsive" v-if="instructors?.length > 0">
      <table class="table table-striped" aria-describedby="instructors">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Name</span></th>
            <th scope="col"><span>Contact</span></th>
            <th scope="col"><span>Has Car</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="instructor in instructors" :key="instructor.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InstructorView', params: { instructorId: instructor.id } }">{{ instructor.id }}</router-link>
            </td>
            <td>{{ instructor.name }}</td>
            <td>{{ instructor.contact }}</td>
            <td>{{ instructor.hasCar }}</td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'InstructorView', params: { instructorId: instructor.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'InstructorEdit', params: { instructorId: instructor.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(instructor)"
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
        <span id="saasenApp.instructor.delete.question" data-cy="instructorDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-instructor-heading">Are you sure you want to delete Instructor {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-instructor"
            data-cy="entityConfirmDeleteButton"
            @click="removeInstructor"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./instructor.component.ts"></script>
