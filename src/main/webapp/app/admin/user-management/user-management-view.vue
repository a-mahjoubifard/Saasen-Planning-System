<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="user">
        <h2 class="jh-entity-heading" data-cy="userManagementDetailsHeading">
          <span>User</span> [<strong>{{ user.login }}</strong
          >]
        </h2>
        <dl class="row-md jh-entity-details">
          <dt><span>Login</span></dt>
          <dd>
            <span>{{ user.login }}</span>
            <b-badge style="margin-left: 10px" :variant="user.activated ? 'success' : 'danger'"
              >Translation missing for ser.activated ? 'userManagement.activated' : 'userManagement.deactivated</b-badge
            >
          </dd>
          <dt><span>First name</span></dt>
          <dd>{{ user.firstName }}</dd>
          <dt><span>Last name</span></dt>
          <dd>{{ user.lastName }}</dd>
          <dt><span>Email</span></dt>
          <dd>{{ user.email }}</dd>
          <dt><span>Created by</span></dt>
          <dd>{{ user.createdBy }}</dd>
          <dt><span>Created date</span></dt>
          <dd>
            <span v-if="user.createdDate">
              {{ formatDate(user.createdDate) }}
            </span>
          </dd>
          <dt><span>Modified by</span></dt>
          <dd>{{ user.lastModifiedBy }}</dd>
          <dt><span>Modified date</span></dt>
          <dd>
            <span v-if="user.lastModifiedDate">
              {{ formatDate(user.lastModifiedDate) }}
            </span>
          </dd>
          <dt><span>Profiles</span></dt>
          <dd>
            <ul class="list-unstyled">
              <li v-for="authority of user.authorities" :key="authority">
                <b-badge variant="info">{{ authority }}</b-badge>
              </li>
            </ul>
          </dd>
        </dl>
        <router-link custom v-slot="{ navigate }" :to="{ name: 'JhiUser' }">
          <button @click="navigate" class="btn btn-info" data-cy="entityDetailsBackButton">
            <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-management-view.component.ts"></script>
