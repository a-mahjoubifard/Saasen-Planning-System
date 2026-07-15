<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.instructor.home.createOrEditLabel" data-cy="InstructorCreateUpdateHeading">Create or edit a Instructor</h2>
        <div>
          <div class="mb-3" v-if="instructor.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="instructor.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="instructor">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="instructor-name"
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
            <label class="form-control-label" for="instructor">Contact</label>
            <input
              type="text"
              class="form-control"
              name="contact"
              id="instructor-contact"
              data-cy="contact"
              :class="{ valid: !v$.contact.$invalid, invalid: v$.contact.$invalid }"
              v-model="v$.contact.$model"
            />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="instructor">Has Car</label>
            <input
              type="checkbox"
              class="form-check"
              name="hasCar"
              id="instructor-hasCar"
              data-cy="hasCar"
              :class="{ valid: !v$.hasCar.$invalid, invalid: v$.hasCar.$invalid }"
              v-model="v$.hasCar.$model"
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
<script lang="ts" src="./instructor-update.component.ts"></script>
