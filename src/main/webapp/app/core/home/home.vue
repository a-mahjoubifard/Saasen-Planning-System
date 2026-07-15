<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4">Saasen Groep!</h1>
      <p class="lead">Veiligheid maak je samen met Saasen Groep!</p>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username">You are logged in as user "{{ username }}".</span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span>If you want to </span>
          <a class="alert-link" @click="showLogin()">sign in</a
          ><span
            >, you can try the default accounts:<br />- Administrator (login="admin" and password="admin") <br />- User (login="user" and
            password="user").</span
          >
        </div>
        <div class="alert alert-warning" v-if="!authenticated">
          <span>You don't have an account yet?</span>&nbsp;
          <router-link class="alert-link" to="/register">Register a new account</router-link>
        </div>
      </div>

      <p>If you have any questions on Saasen Groep:</p>

      <ul>
        <li><a href="https://www.saasen.nl/" target="_blank" rel="noopener noreferrer">Home Page</a></li>
        <li>
          <a href="https://www.saasen.nl/contact" target="_blank" rel="noopener noreferrer">Contact</a>
        </li>
        <li>
          <a href="https://www.saasen.nl/faq" target="_blank" rel="noopener noreferrer">FAQ</a>
        </li>
        <li>
          <a href="https://webshop-saasengroep.nl/" target="_blank" rel="noopener noreferrer">Webshop</a>
        </li>
      </ul>

      <p>
        <span>If you like Saasen, don't forget to give us a star on Trust Pilot</span>
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
