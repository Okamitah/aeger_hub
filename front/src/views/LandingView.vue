<template>
  <div class="landing">

    <LandingNav :portal="portal" @switch="switchPortal" />

    <section class="hero">
      <LandingHero :portal="portal" />

      <AuthCard
        ref="authCardRef"
        :portal="portal"
        :mode="mode"
        :login-username="loginUsername"
        :login-password="loginPassword"
        :login-error="loginError"
        :login-success="loginSuccess"
        :reg="reg"
        :reg-error="regError"
        :reg-success="regSuccess"
        :is-loading="isLoading"
        @update:login-username="loginUsername = $event"
        @update:login-password="loginPassword = $event"
        @switch="switchPortal"
        @mode="m => { mode = m; clearMessages() }"
        @login="handleLogin"
        @register="handleRegister"
        @to-register="scrollToRegister"
      />
    </section>

    <AdminModal
      :visible="showAdminModal"
      :admin-username="adminUsername"
      :admin-password="adminPassword"
      :admin-error="adminError"
      :is-loading="isLoading"
      @update:admin-username="adminUsername = $event"
      @update:admin-password="adminPassword = $event"
      @close="showAdminModal = false"
      @login="handleAdminLogin"
    />

    <div class="secret-trigger" @click="handleSecretClick"></div>

  </div>
</template>

<script setup>
import LandingNav  from './landing/LandingNav.vue'
import LandingHero from './landing/LandingHero.vue'
import AuthCard    from './landing/AuthCard.vue'
import AdminModal  from './landing/AdminModal.vue'
import { useLanding } from './landing/useLanding.js'

const {
  portal, mode,
  loginUsername, loginPassword, loginError, loginSuccess,
  reg, regError, regSuccess,
  isLoading,
  showAdminModal, adminUsername, adminPassword, adminError,
  authCardRef,
  switchPortal, clearMessages, scrollToRegister, handleSecretClick,
  handleLogin, handleAdminLogin, handleRegister,
} = useLanding()
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap');

.landing {
  font-family: 'DM Sans', sans-serif;
  background: #070b12;
  color: #d4dbe8;
  min-height: 100vh;
  overflow-x: hidden;
  position: relative;
}

.landing::before {
  content: '';
  position: fixed; top: -20vh; left: -10vw;
  width: 60vw; height: 60vh;
  background: radial-gradient(ellipse, rgba(56,189,248,0.06) 0%, transparent 70%);
  pointer-events: none; z-index: 0;
}
.landing::after {
  content: '';
  position: fixed; bottom: -10vh; right: -5vw;
  width: 50vw; height: 50vh;
  background: radial-gradient(ellipse, rgba(99,102,241,0.06) 0%, transparent 70%);
  pointer-events: none; z-index: 0;
}

.hero {
  display: grid; grid-template-columns: 1fr 460px; gap: 60px;
  align-items: start; padding: 80px 48px 100px;
  max-width: 1280px; margin: 0 auto; position: relative; z-index: 1;
}

.secret-trigger { position: fixed; top: 0; left: 0; width: 160px; height: 60px; z-index: 300; cursor: default; }

@media (max-width: 900px) {
  .hero { grid-template-columns: 1fr; padding: 40px 24px 60px; gap: 40px; }
}
</style>