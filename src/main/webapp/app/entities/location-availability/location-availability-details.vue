<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="locationAvailability">
        <h2 class="jh-entity-heading" data-cy="locationAvailabilityDetailsHeading">
          <span>Location AvailabilityLocationAvailability</span> {{ locationAvailability.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ locationAvailability.title }}</span>
          </dd>
          <dt>
            <span>Available From</span>
          </dt>
          <dd>
            <span v-if="locationAvailability.availableFrom">{{ formatDateLong(locationAvailability.availableFrom) }}</span>
          </dd>
          <dt>
            <span>Available To</span>
          </dt>
          <dd>
            <span v-if="locationAvailability.availableTo">{{ formatDateLong(locationAvailability.availableTo) }}</span>
          </dd>
          <dt>
            <span>Location</span>
          </dt>
          <dd>
            <div v-if="locationAvailability.location">
              <router-link :to="{ name: 'LocationView', params: { locationId: locationAvailability.location.id } }">{{
                locationAvailability.location.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="locationAvailability.id"
          :to="{ name: 'LocationAvailabilityEdit', params: { locationAvailabilityId: locationAvailability.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./location-availability-details.component.ts"></script>
