<template>
  <div>
    <h1>AegerHub</h1>
    
    <p>Backend status: {{ healthStatus }}</p>

    <div v-if="!isLoggedIn">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div>
          <input v-model="username" type="text" placeholder="Username" required />
        </div>
        <div>
          <input v-model="password" type="password" placeholder="Password" required />
        </div>
        <button type="submit" :disabled="isLoggingIn">
          {{ isLoggingIn ? 'Logging in...' : 'Login' }}
        </button>
        <div v-if="loginError">{{ loginError }}</div>
      </form>
    </div>

    <div v-else>
      <h2>Welcome, {{ username }}!</h2>
      <button @click="logout">Logout</button>

      <div>
        <h3>Patient Generator</h3>
        
        <div>
          <label>
            Number of patients to generate:
            <input 
              v-model.number="patientCount" 
              type="number" 
              min="1" 
              max="100"
            />
          </label>
          
          <button @click="generatePatients" :disabled="isGenerating">
            {{ isGenerating ? 'Generating...' : 'Generate Patients' }}
          </button>
          
          <button @click="loadPatients" :disabled="isLoading">
            {{ isLoading ? 'Loading...' : 'Refresh Patient List' }}
          </button>
        </div>

        <div v-if="generationMessage">
          {{ generationMessage }}
        </div>

        <div v-if="patients.length > 0">
          <h4>Patients ({{ patients.length }} total)</h4>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Sex</th>
                <th>Height (cm)</th>
                <th>Weight (kg)</th>
                <th>BMI</th>
                <th>Illness</th>
                <th>Sleep Quality</th>
                <th>Athleticism</th>
                <th>Smoker</th>
                <th>Drinker</th>
                <th>BPM Max</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="patient in patients" :key="patient.id">
                <td>{{ patient.id }}</td>
                <td>{{ patient.name }}</td>
                <td>{{ patient.sex }}</td>
                <td>{{ patient.heightCm.toFixed(1) }}</td>
                <td>{{ patient.weightKg.toFixed(1) }}</td>
                <td>{{ calculateBMI(patient).toFixed(1) }}</td>
                <td>{{ patient.illness }}</td>
                <td>{{ patient.sleepQuality }}</td>
                <td>{{ patient.athleticism }}</td>
                <td>{{ patient.smoker ? '✓' : '✗' }}</td>
                <td>{{ patient.drinker ? '✓' : '✗' }}</td>
                <td>{{ patient.bpmMax }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="!isLoading">
          No patients in database. Generate some patients to get started!
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const healthStatus = ref('Checking...')
const username = ref('')
const password = ref('')
const isLoggingIn = ref(false)
const loginError = ref('')
const isLoggedIn = ref(false)

const patients = ref([])
const patientCount = ref(5)
const isGenerating = ref(false)
const isLoading = ref(false)
const generationMessage = ref('')

onMounted(async () => {
  try {
    const res = await fetch('/api/')
    if (res.ok) {
      const data = await res.json()
      healthStatus.value = data.message || 'OK'
    } else {
      throw new Error()
    }
  } catch {
    healthStatus.value = 'Offline'
  }
})

async function handleLogin() {
    isLoggingIn.value = true
    loginError.value = ''
    try {
        const res = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username.value.trim(),
                password: password.value.trim()
            })
        })
        const data = await res.json()
        if (data.success) {
            isLoggedIn.value = true
            await loadPatients()
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
  patients.value = []
}

async function generatePatients() {
  isGenerating.value = true
  generationMessage.value = ''
  
  try {
    const endpoint = patientCount.value === 1 
      ? '/patients/mock' 
      : `/patients/mock/${patientCount.value}`
    
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    })
    
    if (res.ok) {
      generationMessage.value = `Successfully generated ${patientCount.value} patient(s)!`
      await loadPatients()
    } else {
      throw new Error('Generation failed')
    }
  } catch (error) {
    generationMessage.value = 'Error generating patients'
  } finally {
    isGenerating.value = false
  }
}

async function loadPatients() {
  isLoading.value = true
  generationMessage.value = ''
  
  try {
    const res = await fetch('/patients')
    if (res.ok) {
      const data = await res.json()
      patients.value = data
    } else {
      throw new Error('Failed to load patients')
    }
  } catch (error) {
    generationMessage.value = 'Error loading patients'
  } finally {
    isLoading.value = false
  }
}

function calculateBMI(patient) {
  const heightInMeters = patient.heightCm / 100
  return patient.weightKg / (heightInMeters * heightInMeters)
}
</script>
