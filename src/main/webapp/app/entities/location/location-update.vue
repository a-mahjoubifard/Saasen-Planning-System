<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.location.home.createOrEditLabel" data-cy="LocationCreateUpdateHeading">Create or edit a Location</h2>
        <div>
          <div class="mb-3" v-if="location.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="location.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="location-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location">Address</label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="location-address"
              data-cy="address"
              :class="{ valid: !v$.address.$invalid, invalid: v$.address.$invalid }"
              v-model="v$.address.$model"
            />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location">Capacity</label>
            <input
              type="number"
              class="form-control"
              name="capacity"
              id="location-capacity"
              data-cy="capacity"
              :class="{ valid: !v$.capacity.$invalid, invalid: v$.capacity.$invalid }"
              v-model.number="v$.capacity.$model"
            />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="location">Equipment Type</label>
            <input
              type="text"
              class="form-control"
              name="equipmentType"
              id="location-equipmentType"
              data-cy="equipmentType"
              :class="{ valid: !v$.equipmentType.$invalid, invalid: v$.equipmentType.$invalid }"
              v-model="v$.equipmentType.$model"
            />
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
<script lang="ts" src="./location-update.component.ts"></script>
