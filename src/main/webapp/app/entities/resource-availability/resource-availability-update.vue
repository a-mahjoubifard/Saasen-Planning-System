<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.resourceAvailability.home.createOrEditLabel" data-cy="ResourceAvailabilityCreateUpdateHeading">
          Create or edit a Resource Availability
        </h2>
        <div>
          <div class="mb-3" v-if="resourceAvailability.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="resourceAvailability.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="resource-availability">Available From</label>
            <div class="d-flex">
              <input
                id="resource-availability-availableFrom"
                data-cy="availableFrom"
                type="datetime-local"
                class="form-control"
                name="availableFrom"
                :class="{ valid: !v$.availableFrom.$invalid, invalid: v$.availableFrom.$invalid }"
                :value="convertDateTimeFromServer(v$.availableFrom.$model)"
                @change="updateInstantField('availableFrom', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="resource-availability">Available To</label>
            <div class="d-flex">
              <input
                id="resource-availability-availableTo"
                data-cy="availableTo"
                type="datetime-local"
                class="form-control"
                name="availableTo"
                :class="{ valid: !v$.availableTo.$invalid, invalid: v$.availableTo.$invalid }"
                :value="convertDateTimeFromServer(v$.availableTo.$model)"
                @change="updateInstantField('availableTo', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="resource-availability">Resource</label>
            <select
              class="form-control"
              id="resource-availability-resource"
              data-cy="resource"
              name="resource"
              v-model="resourceAvailability.resource"
            >
              <option :value="null"></option>
              <option
                :value="
                  resourceAvailability.resource && resourceOption.id === resourceAvailability.resource.id
                    ? resourceAvailability.resource
                    : resourceOption
                "
                v-for="resourceOption in resources"
                :key="resourceOption.id"
              >
                {{ resourceOption.name }}
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
<script lang="ts" src="./resource-availability-update.component.ts"></script>
