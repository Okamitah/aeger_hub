<template>
  <div>
    <h1>AegerHub</h1>
    
    <div :style="{color: healthStatusColor, marginBottom: '20px'}">
      Backend status: {{ healthStatus }}
    </div>

    <div v-if="!isLoggedIn">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div style="margin-bottom:12px">
          <input v-model="username" type="text" placeholder="Username" required />
        </div>
        <div style="margin-bottom:16px">
          <input v-model="password" type="password" placeholder="Password" required />
        </div>
        <button type="submit" :disabled="isLoggingIn"">
          {{ isLoggingIn ? 'Logging in...' : 'Login' }}
        </button>
        <div v-if="loginError">{{ loginError }}</div>
      </form>
    </div>

    <div v-else>
      <h2>Welcome, {{ username }}!</h2>
      <button @click="logout">
        Logout
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const healthStatus = ref('Checking...')
const healthStatusColor = ref('orange')
const username = ref('')
const password = ref('')
const isLoggingIn = ref(false)
const loginError = ref('')
const isLoggedIn = ref(false)

onMounted(async () => {
  try {
    const res = await fetch('/api/')
    if (res.ok) {
      const data = await res.json()
      healthStatus.value = data.message || 'OK'
      healthStatusColor.value = 'green'
    } else {
      throw new Error()
    }
  } catch {
    healthStatus.value = 'Offline'
    healthStatusColor.value = 'red'
  }
})

async function handleLogin() {
  isLoggingIn.value = true
  loginError.value = ''
  try {
    const res = await fetch('/login/', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })
    const data = await res.json()
    if (data.success) {
      isLoggedIn.value = true
    } else {
      loginError.value = data.message || 'Login failed'
    }
  } catch {
    loginError.value = 'Network error'
  } finally {
    isLoggingIn.value = false
  }
}

function logout() {
  isLoggedIn.value = false
  username.value = ''
  password.value = ''
}
</script>
