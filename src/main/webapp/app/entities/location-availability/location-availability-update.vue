<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.locationAvailability.home.createOrEditLabel" data-cy="LocationAvailabilityCreateUpdateHeading">
          Create or edit a Location Availability
        </h2>
        <div>
          <div class="mb-3" v-if="locationAvailability.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="locationAvailability.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location-availability">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="location-availability-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location-availability">Available From</label>
            <div class="d-flex">
              <input
                id="location-availability-availableFrom"
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
            <label class="form-control-label" for="location-availability">Available To</label>
            <div class="d-flex">
              <input
                id="location-availability-availableTo"
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
            <label class="form-control-label" for="location-availability">Location</label>
            <select
              class="form-control"
              id="location-availability-location"
              data-cy="location"
              name="location"
              v-model="locationAvailability.location"
            >
              <option :value="null"></option>
              <option
                :value="
                  locationAvailability.location && locationOption.id === locationAvailability.location.id
                    ? locationAvailability.location
                    : locationOption
                "
                v-for="locationOption in locations"
                :key="locationOption.id"
              >
                {{ locationOption.name }}
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
<script lang="ts" src="./location-availability-update.component.ts"></script>
