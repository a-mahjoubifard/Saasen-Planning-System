<template>
  <div>
    <h2 id="page-heading" data-cy="FreelancerHeading">
      <span id="freelancer">Freelancers</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'FreelancerCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-freelancer"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Freelancer</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && freelancers?.length === 0">
      <span>No Freelancers found</span>
    </div>
    <div class="table-responsive" v-if="freelancers?.length > 0">
      <table class="table table-striped" aria-describedby="freelancers">
        <thead>
          <tr>
            <th scope="col"><span>ID</span></th>
            <th scope="col"><span>Name</span></th>
            <th scope="col"><span>Contact</span></th>
            <th scope="col"><span>Qualification</span></th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="freelancer in freelancers" :key="freelancer.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'FreelancerView', params: { freelancerId: freelancer.id } }">{{ freelancer.id }}</router-link>
            </td>
            <td>{{ freelancer.name }}</td>
            <td>{{ freelancer.contact }}</td>
            <td>{{ freelancer.qualification }}</td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'FreelancerView', params: { freelancerId: freelancer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'FreelancerEdit', params: { freelancerId: freelancer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(freelancer)"
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
        <span id="saasenApp.freelancer.delete.question" data-cy="freelancerDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-freelancer-heading">Are you sure you want to delete Freelancer {{ removeId }}?</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-freelancer"
            data-cy="entityConfirmDeleteButton"
            @click="removeFreelancer"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./freelancer.component.ts"></script>
