<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="saasenApp.course.home.createOrEditLabel" data-cy="CourseCreateUpdateHeading">Create or edit a Course</h2>
        <div>
          <div class="mb-3" v-if="course.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="course.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="course">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="course-title"
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
            <label class="form-control-label" for="course">Duration</label>
            <input
              type="number"
              class="form-control"
              name="duration"
              id="course-duration"
              data-cy="duration"
              :class="{ valid: !v$.duration.$invalid, invalid: v$.duration.$invalid }"
              v-model.number="v$.duration.$model"
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
<script lang="ts" src="./course-update.component.ts"></script>
