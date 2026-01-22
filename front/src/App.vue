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
          <strong>{{ generationMessage }}</strong>
        </div>

        <div v-if="errorDetails">
          <p>Error details:</p>
          <pre>{{ errorDetails }}</pre>
        </div>

        <div v-if="patients.length > 0">
          <h4>Patients ({{ patients.length }} total)</h4>
          <table border="1">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Birth Date</th>
                <th>Age</th>
                <th>Sex</th>
                <th>Height (cm)</th>
                <th>Weight (kg)</th>
                <th>BMI</th>
                <th>Illness</th>
                <th>Sleep Quality</th>
                <th>Athleticism</th>
                <th>Activity State</th>
                <th>Smoker</th>
                <th>Drinker</th>
                <th>BPM Max</th>
                <th>Tracking</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="patient in patients" :key="patient.id">
                <td>{{ patient.id }}</td>
                <td>{{ patient.name }}</td>
                <td>{{ patient.birthDate || 'N/A' }}</td>
                <td>{{ calculateAge(patient.birthDate) }}</td>
                <td>{{ patient.sex }}</td>
                <td>{{ patient.heightCm ? patient.heightCm.toFixed(1) : 'N/A' }}</td>
                <td>{{ patient.weightKg ? patient.weightKg.toFixed(1) : 'N/A' }}</td>
                <td>{{ patient.heightCm && patient.weightKg ? calculateBMI(patient).toFixed(1) : 'N/A' }}</td>
                <td>{{ patient.illness }}</td>
                <td>{{ patient.sleepQuality }}</td>
                <td>{{ patient.athleticism }}</td>
                <td>{{ patient.currentActivityState || 'N/A' }}</td>
                <td>{{ patient.smoker ? '✓' : '✗' }}</td>
                <td>{{ patient.drinker ? '✓' : '✗' }}</td>
                <td>{{ patient.bpmMax }}</td>
                <td>{{ patient.trackingEnabled ? '✓' : '✗' }}</td>
                <td>
                  <button @click="viewMealPlan(patient)">View Meal Plan</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="!isLoading">
          No patients in database. Generate some patients to get started!
        </div>
      </div>

      <!-- Meal Recommendations Section -->
      <div v-if="selectedPatient">
        <hr>
        <h3>Meal Plan for {{ selectedPatient.name }}</h3>
        <p>Condition: <strong>{{ selectedPatient.illness }}</strong></p>
        <button @click="selectedPatient = null">Close Meal Plan</button>
        
        <MealRecommendations 
          v-if="selectedPatient.illness" 
          :illness="selectedPatient.illness" 
          :token="jwtToken" 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MealRecommendations from './MealRecommendations.vue'

const healthStatus = ref('Checking...')
const username = ref('')
const password = ref('')
const isLoggingIn = ref(false)
const loginError = ref('')
const isLoggedIn = ref(false)
const jwtToken = ref('')

const patients = ref([])
const patientCount = ref(5)
const isGenerating = ref(false)
const isLoading = ref(false)
const generationMessage = ref('')
const errorDetails = ref('')
const selectedPatient = ref(null)

onMounted(async () => {
  const storedToken = localStorage.getItem('jwt_token')
  const storedUsername = localStorage.getItem('username')
  
  if (storedToken && storedUsername) {
    jwtToken.value = storedToken
    username.value = storedUsername
    isLoggedIn.value = true
  }

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

  if (isLoggedIn.value) {
    await loadPatients()
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
        if (data.success && data.token) {
            isLoggedIn.value = true
            jwtToken.value = data.token
            username.value = data.username
            
            localStorage.setItem('jwt_token', data.token)
            localStorage.setItem('username', data.username)
            
            await loadPatients()
        } else {
            loginError.value = data.message || 'Login failed'
        }
    } catch (err) {
        loginError.value = 'Network error: ' + err.message
    } finally {
        isLoggingIn.value = false
    }
}

function logout() {
  isLoggedIn.value = false
  jwtToken.value = ''
  username.value = ''
  password.value = ''
  patients.value = []
  generationMessage.value = ''
  errorDetails.value = ''
  selectedPatient.value = null
  
  localStorage.removeItem('jwt_token')
  localStorage.removeItem('username')
}

function getAuthHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${jwtToken.value}`
  }
}

async function generatePatients() {
  isGenerating.value = true
  generationMessage.value = 'Generating patients...'
  errorDetails.value = ''
  
  try {
    const endpoint = patientCount.value === 1 
      ? '/patients/mock' 
      : `/patients/mock/${patientCount.value}`
    
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: getAuthHeaders()
    })
    
    if (res.ok) {
      generationMessage.value = `Successfully generated ${patientCount.value} patient(s)!`
      await loadPatients()
    } else if (res.status === 401 || res.status === 403) {
      generationMessage.value = 'Authentication failed. Please log in again.'
      logout()
    } else {
      const errorText = await res.text()
      errorDetails.value = `Status: ${res.status}\n${errorText}`
      throw new Error('Generation failed')
    }
  } catch (error) {
    generationMessage.value = 'Error generating patients'
    if (!errorDetails.value) {
      errorDetails.value = error.message
    }
  } finally {
    isGenerating.value = false
  }
}

async function loadPatients() {
  isLoading.value = true
  generationMessage.value = ''
  errorDetails.value = ''
  
  try {
    const res = await fetch('/patients', {
      headers: getAuthHeaders()
    })
    
    if (res.ok) {
      const data = await res.json()
      patients.value = data
    } else if (res.status === 401 || res.status === 403) {
      generationMessage.value = 'Authentication failed. Please log in again.'
      logout()
    } else {
      const errorText = await res.text()
      errorDetails.value = `Status: ${res.status}\n${errorText}`
      throw new Error('Failed to load patients')
    }
  } catch (error) {
    generationMessage.value = 'Error loading patients'
    if (!errorDetails.value) {
      errorDetails.value = error.message
    }
  } finally {
    isLoading.value = false
  }
}

function viewMealPlan(patient) {
  selectedPatient.value = patient
  window.scrollTo(0, document.body.scrollHeight)
}

function calculateBMI(patient) {
  if (!patient.heightCm || !patient.weightKg) return 0
  const heightInMeters = patient.heightCm / 100
  return patient.weightKg / (heightInMeters * heightInMeters)
}

function calculateAge(birthDate) {
  if (!birthDate) return 'N/A'
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age
}
</script>
