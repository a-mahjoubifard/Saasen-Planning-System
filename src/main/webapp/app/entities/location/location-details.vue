<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="location">
        <h2 class="jh-entity-heading" data-cy="locationDetailsHeading"><span>LocationLocation</span> {{ location.id }}</h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ location.name }}</span>
          </dd>
          <dt>
            <span>Address</span>
          </dt>
          <dd>
            <span>{{ location.address }}</span>
          </dd>
          <dt>
            <span>Capacity</span>
          </dt>
          <dd>
            <span>{{ location.capacity }}</span>
          </dd>
          <dt>
            <span>Equipment Type</span>
          </dt>
          <dd>
            <span>{{ location.equipmentType }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="location.id" :to="{ name: 'LocationEdit', params: { locationId: location.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./location-details.component.ts"></script>
