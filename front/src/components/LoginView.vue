<template>
  <div class="login-wrap">
    <div class="login-card">
      <h2>Sign in</h2>
      <div class="field">
        <input v-model="inputUsername" type="text" placeholder="Username" @keyup.enter="handleLogin" />
      </div>
      <div class="field">
        <input v-model="inputPassword" type="password" placeholder="Password" @keyup.enter="handleLogin" />
      </div>
      <button class="btn-primary" @click="handleLogin" :disabled="isLoggingIn">
        {{ isLoggingIn ? 'Signing in…' : 'Sign in' }}
      </button>
      <div v-if="loginError" class="msg error">{{ loginError }}</div>
      <div v-if="accessDenied" class="msg error">⛔ Admin access only.</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { isLoggedIn, jwtToken, username, isAdmin } from '../useAuth.js'

const emit = defineEmits(['login-success'])

const inputUsername = ref('')
const inputPassword = ref('')
const isLoggingIn = ref(false)
const loginError = ref('')
const accessDenied = ref(false)

async function handleLogin() {
  isLoggingIn.value = true
  loginError.value = ''
  accessDenied.value = false
  try {
    const res = await fetch('/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: inputUsername.value.trim(), password: inputPassword.value.trim() })
    })
    const data = await res.json()
    if (data.success && data.token) {
      if (data.username !== 'admin') {
        accessDenied.value = true
        return
      }
      jwtToken.value = data.token
      username.value = data.username
      isAdmin.value = true
      isLoggedIn.value = true
      localStorage.setItem('jwt_token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('is_admin', 'true')
      emit('login-success')
    } else {
      loginError.value = data.message || 'Login failed'
    }
  } catch (err) {
    loginError.value = 'Network error: ' + err.message
  } finally {
    isLoggingIn.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 53px);
}

.login-card {
  background: #13161f;
  border: 1px solid #1e2330;
  border-radius: 10px;
  padding: 36px 40px;
  width: 340px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.login-card h2 {
  font-size: 18px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 4px;
}

.field input {
  width: 100%;
  background: #0d0f14;
  border: 1px solid #2a3045;
  border-radius: 6px;
  color: #c8cdd8;
  padding: 9px 12px;
  font-family: inherit;
  font-size: 13px;
  outline: none;
  transition: border-color 0.2s;
}
.field input:focus { border-color: #3b82f6; }

.btn-primary {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 9px 16px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { opacity: 0.4; cursor: not-allowed; }

.msg { padding: 8px 12px; border-radius: 6px; font-size: 12px; font-weight: 500; }
.msg.error { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
</style>