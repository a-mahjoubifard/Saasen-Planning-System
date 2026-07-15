<template>
  <div>
    <div class="d-flex justify-content-center">
      <div class="col-md-8 toastify-container">
        <h1 id="register-title" data-cy="registerTitle">Registration</h1>

        <div class="alert alert-success" role="alert" v-if="success">
          <strong>Registration saved!</strong> Please check your email for confirmation.
        </div>

        <div class="alert alert-danger" role="alert" v-if="error"><strong>Registration failed!</strong> Please try again later.</div>

        <div class="alert alert-danger" role="alert" v-if="errorUserExists">
          <strong>Login name already registered!</strong> Please choose another one.
        </div>

        <div class="alert alert-danger" role="alert" v-if="errorEmailExists">
          <strong>Email is already in use!</strong> Please choose another one.
        </div>
      </div>
    </div>
    <div class="d-flex justify-content-center">
      <div class="col-md-8">
        <form id="register-form" name="registerForm" @submit.prevent="register()" v-if="!success" no-validate>
          <div class="mb-3">
            <label class="form-control-label" for="username">Username</label>
            <input
              type="text"
              class="form-control"
              v-model="v$.registerAccount.login.$model"
              id="username"
              name="login"
              :class="{ 'is-valid': !v$.registerAccount.login.$invalid, 'is-invalid': v$.registerAccount.login.$invalid }"
              required
              minlength="1"
              maxlength="50"
              pattern="^[a-zA-Z0-9!#$&'*+=?^_`{|}~.-]+@?[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$"
              placeholder="Your username"
              data-cy="username"
            />
            <div v-if="v$.registerAccount.login.$anyDirty && v$.registerAccount.login.$invalid">
              <small class="form-text text-danger" v-if="v$.registerAccount.login.required.$invalid">Your username is required.</small>
              <small class="form-text text-danger" v-if="v$.registerAccount.login.minLength.$invalid"
                >Your username is required to be at least 1 character.</small
              >
              <small class="form-text text-danger" v-if="v$.registerAccount.login.maxLength.$invalid"
                >Your username cannot be longer than 50 characters.</small
              >
              <small class="form-text text-danger" v-if="v$.registerAccount.login.pattern.$invalid">Your username is invalid.</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="email">Email</label>
            <input
              type="email"
              class="form-control"
              id="email"
              name="email"
              :class="{ 'is-valid': !v$.registerAccount.email.$invalid, 'is-invalid': v$.registerAccount.email.$invalid }"
              v-model="v$.registerAccount.email.$model"
              minlength="5"
              maxlength="254"
              email
              required
              placeholder="Your email"
              data-cy="email"
            />
            <div v-if="v$.registerAccount.email.$anyDirty && v$.registerAccount.email.$invalid">
              <small class="form-text text-danger" v-if="v$.registerAccount.email.required.$invalid">Your email is required.</small>
              <small class="form-text text-danger" v-if="v$.registerAccount.email.email.$invalid">Your email is invalid.</small>
              <small class="form-text text-danger" v-if="v$.registerAccount.email.minLength.$invalid"
                >Your email is required to be at least 5 characters.</small
              >
              <small class="form-text text-danger" v-if="v$.registerAccount.email.maxLength.$invalid"
                >Your email cannot be longer than 50 characters.</small
              >
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="firstPassword">New password</label>
            <input
              type="password"
              class="form-control"
              id="firstPassword"
              name="password"
              :class="{ 'is-valid': !v$.registerAccount.password.$invalid, 'is-invalid': v$.registerAccount.password.$invalid }"
              v-model="v$.registerAccount.password.$model"
              minlength="4"
              maxlength="50"
              required
              placeholder="New password"
              data-cy="firstPassword"
            />
            <div v-if="v$.registerAccount.password.$anyDirty && v$.registerAccount.password.$invalid">
              <small class="form-text text-danger" v-if="v$.registerAccount.password.required.$invalid">Your password is required.</small>
              <small class="form-text text-danger" v-if="v$.registerAccount.password.minLength.$invalid"
                >Your password is required to be at least 4 characters.</small
              >
              <small class="form-text text-danger" v-if="v$.registerAccount.password.maxLength.$invalid"
                >Your password cannot be longer than 50 characters.</small
              >
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="secondPassword">New password confirmation</label>
            <input
              type="password"
              class="form-control"
              id="secondPassword"
              name="confirmPasswordInput"
              :class="{ 'is-valid': !v$.confirmPassword.$invalid, 'is-invalid': v$.confirmPassword.$invalid }"
              v-model="v$.confirmPassword.$model"
              minlength="4"
              maxlength="50"
              required
              placeholder="Confirm the new password"
              data-cy="secondPassword"
            />
            <div v-if="v$.confirmPassword.$dirty && v$.confirmPassword.$invalid">
              <small class="form-text text-danger" v-if="v$.confirmPassword.required.$invalid"
                >Your confirmation password is required.</small
              >
              <small class="form-text text-danger" v-if="v$.confirmPassword.minLength.$invalid"
                >Your confirmation password is required to be at least 4 characters.</small
              >
              <small class="form-text text-danger" v-if="v$.confirmPassword.maxLength.$invalid"
                >Your confirmation password cannot be longer than 50 characters.</small
              >
              <small class="form-text text-danger" v-if="v$.confirmPassword.sameAsPassword"
                >The password and its confirmation do not match!</small
              >
            </div>
          </div>

          <button type="submit" :disabled="v$.$invalid" class="btn btn-primary" data-cy="submit">Register</button>
        </form>
        <p></p>
        <div class="alert alert-warning">
          <span>If you want to </span>
          <a class="alert-link" @click="showLogin()">sign in</a
          ><span
            >, you can try the default accounts:<br />- Administrator (login="admin" and password="admin") <br />- User (login="user" and
            password="user").</span
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./register.component.ts"></script>
