<template>
  <div class="d-flex justify-content-center">
    <div class="row-md">
      <div class="col-md-12">
        <b-alert data-cy="loginError" variant="danger" :model-value="authenticationError"
          ><strong>Failed to sign in!</strong> Please check your credentials and try again.</b-alert
        >
      </div>
      <div class="col-md-12">
        <b-form @submit.prevent="doLogin()">
          <b-form-group label="Username" label-for="username">
            <b-form-input id="username" type="text" name="username" placeholder="Your username" v-model="login" data-cy="username">
            </b-form-input>
          </b-form-group>
          <b-form-group label="Password" label-for="password">
            <b-form-input id="password" type="password" name="password" placeholder="Your password" v-model="password" data-cy="password">
            </b-form-input>
          </b-form-group>
          <b-form-checkbox id="rememberMe" name="rememberMe" v-model="rememberMe" checked>
            <span>Remember me</span>
          </b-form-checkbox>
          <div>
            <b-button data-cy="submit" type="submit" variant="primary">Sign in</b-button>
          </div>
        </b-form>
        <p></p>
        <div>
          <b-alert :model-value="true" variant="warning">
            <b-link :to="'/account/reset/request'" class="alert-link" data-cy="forgetYourPasswordSelector"
              >Did you forget your password?</b-link
            >
          </b-alert>
        </div>
        <div>
          <b-alert :model-value="true" variant="warning">
            <span>You don't have an account yet?</span>
            <b-link :to="'/register'" class="alert-link">Register a new account</b-link>
          </b-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./login-form.component.ts"></script>
