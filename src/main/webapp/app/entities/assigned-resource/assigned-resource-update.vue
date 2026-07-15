<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.assignedResource.home.createOrEditLabel" data-cy="AssignedResourceCreateUpdateHeading">
          Create or edit a Assigned Resource
        </h2>
        <div>
          <div class="mb-3" v-if="assignedResource.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="assignedResource.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="assigned-resource">Resource Availability</label>
            <select
              class="form-control"
              id="assigned-resource-resourceAvailability"
              data-cy="resourceAvailability"
              name="resourceAvailability"
              v-model="assignedResource.resourceAvailability"
            >
              <option :value="null"></option>
              <option
                :value="
                  assignedResource.resourceAvailability && resourceAvailabilityOption.id === assignedResource.resourceAvailability.id
                    ? assignedResource.resourceAvailability
                    : resourceAvailabilityOption
                "
                v-for="resourceAvailabilityOption in resourceAvailabilities"
                :key="resourceAvailabilityOption.id"
              >
                {{ resourceAvailabilityOption.id }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="assigned-resource">Session</label>
            <select class="form-control" id="assigned-resource-session" data-cy="session" name="session" v-model="assignedResource.session">
              <option :value="null"></option>
              <option
                :value="
                  assignedResource.session && sessionOption.id === assignedResource.session.id ? assignedResource.session : sessionOption
                "
                v-for="sessionOption in sessions"
                :key="sessionOption.id"
              >
                {{ sessionOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./assigned-resource-update.component.ts"></script>
