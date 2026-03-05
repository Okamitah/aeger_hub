<template>
  <div class="app">
    <header class="app-header">
      <span class="logo">⬡ AegerHub</span>
      <span class="backend-status" :class="healthStatus === 'Offline' ? 'offline' : 'online'">
        {{ healthStatus }}
      </span>
    </header>

    <div v-if="!isLoggedIn" class="login-wrap">
      <div class="login-card">
        <h2>Sign in</h2>
        <div class="field">
          <input v-model="username" type="text" placeholder="Username" required />
        </div>
        <div class="field">
          <input v-model="password" type="password" placeholder="Password" required />
        </div>
        <button class="btn-primary" @click="handleLogin" :disabled="isLoggingIn">
          {{ isLoggingIn ? 'Signing in…' : 'Sign in' }}
        </button>
        <div v-if="loginError" class="msg error">{{ loginError }}</div>
      </div>
    </div>

    <div v-else class="main-content">

      <div class="top-bar">
        <span class="welcome">{{ username }}</span>
        <button class="btn-ghost" @click="logout">Logout</button>
      </div>

      <!-- PATIENTS -->
      <section class="section">
        <div class="section-header">
          <h2>Patients</h2>
          <div class="controls">
            <input v-model.number="patientCount" type="number" min="1" max="100" class="count-input" />
            <button class="btn-primary" @click="generatePatients" :disabled="isGenerating">
              {{ isGenerating ? 'Generating…' : 'Generate' }}
            </button>
            <button class="btn-ghost" @click="loadPatients" :disabled="isLoading">
              {{ isLoading ? '…' : '↺ Refresh' }}
            </button>
          </div>
        </div>
        <div v-if="generationMessage" class="msg" :class="errorDetails ? 'error' : 'success'">{{ generationMessage }}</div>
        <div v-if="errorDetails" class="msg error"><pre>{{ errorDetails }}</pre></div>

        <div v-if="patients.length > 0" class="table-wrap">
          <table>
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
                <th>Sleep</th>
                <th>Athleticism</th>
                <th>Activity</th>
                <th>Smoker</th>
                <th>Drinker</th>
                <th>BPM Max</th>
                <th>Tracking</th>
                <th>Blood Test</th>
                <th>Meal Plan</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="patient in patients" :key="patient.id" :class="{ 'row-active': selectedBloodPatient && selectedBloodPatient.id === patient.id }">
                <td class="mono">{{ patient.id }}</td>
                <td>{{ patient.name }}</td>
                <td>{{ patient.birthDate || 'N/A' }}</td>
                <td>{{ calculateAge(patient.birthDate) }}</td>
                <td>{{ patient.sex }}</td>
                <td>{{ patient.heightCm ? patient.heightCm.toFixed(1) : 'N/A' }}</td>
                <td>{{ patient.weightKg ? patient.weightKg.toFixed(1) : 'N/A' }}</td>
                <td>{{ patient.heightCm && patient.weightKg ? calculateBMI(patient).toFixed(1) : 'N/A' }}</td>
                <td><span class="tag">{{ patient.illness }}</span></td>
                <td>{{ patient.sleepQuality }}</td>
                <td>{{ patient.athleticism }}</td>
                <td>{{ patient.currentActivityState || 'N/A' }}</td>
                <td>{{ patient.smoker ? '✓' : '✗' }}</td>
                <td>{{ patient.drinker ? '✓' : '✗' }}</td>
                <td>{{ patient.bpmMax }}</td>
                <td>{{ patient.trackingEnabled ? '✓' : '✗' }}</td>
                <td>
                  <button
                    class="btn-accent"
                    @click="generateBloodTest(patient)"
                    :disabled="bloodTestLoadingId === patient.id"
                  >
                    {{ bloodTestLoadingId === patient.id ? '…' : '🩸 Generate' }}
                  </button>
                </td>
                <td>
                  <button class="btn-ghost-sm" @click="viewMealPlan(patient)">Meal Plan</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="!isLoading" class="empty">No patients yet. Generate some to get started.</div>
      </section>

      <!-- DOCTORS -->
      <section class="section">
        <div class="section-header">
          <h2>Doctors</h2>
          <div class="controls">
            <input v-model.number="doctorCount" type="number" min="1" max="100" class="count-input" />
            <button class="btn-primary" @click="generateDoctors" :disabled="isDoctorGenerating">
              {{ isDoctorGenerating ? 'Generating…' : 'Generate' }}
            </button>
            <button class="btn-ghost" @click="loadDoctors" :disabled="isDoctorLoading">
              {{ isDoctorLoading ? '…' : '↺ Refresh' }}
            </button>
          </div>
        </div>
        <div v-if="doctorGenerationMessage" class="msg" :class="doctorErrorDetails ? 'error' : 'success'">{{ doctorGenerationMessage }}</div>

        <div v-if="doctors.length > 0" class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Sex</th>
                <th>Specialty</th>
                <th>Experience (yrs)</th>
                <th>Hospital</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="doctor in doctors" :key="doctor.id">
                <td class="mono">{{ doctor.id }}</td>
                <td>{{ doctor.name }}</td>
                <td>{{ doctor.sex }}</td>
                <td>{{ doctor.specialty }}</td>
                <td>{{ doctor.yearsOfExperience }}</td>
                <td>{{ doctor.hospital }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="!isDoctorLoading" class="empty">No doctors yet.</div>
      </section>

      <AlimentManager :token="jwtToken" />

      <!-- MEAL PLAN -->
      <div v-if="selectedPatient" class="panel">
        <div class="panel-header">
          <h3>Meal Plan — {{ selectedPatient.name }}</h3>
          <button class="btn-ghost" @click="selectedPatient = null">✕ Close</button>
        </div>
        <p>Condition: <strong>{{ selectedPatient.illness }}</strong></p>
        <MealRecommendations
          v-if="selectedPatient.illness"
          :illness="selectedPatient.illness"
          :token="jwtToken"
        />
      </div>

      <!-- BLOOD TEST RESULTS -->
      <div v-if="selectedBloodPatient" ref="bloodPanel" class="panel blood-panel">
        <div class="panel-header">
          <h3>🩸 Blood Test — {{ selectedBloodPatient.name }}</h3>
          <button class="btn-ghost" @click="selectedBloodPatient = null; bloodTestResult = null">✕ Close</button>
        </div>

        <div v-if="bloodTestResult" class="blood-grid">
          <div class="blood-card" v-for="marker in bloodMarkers" :key="marker.key">
            <div class="blood-label">{{ marker.label }}</div>
            <div class="blood-value" :class="getMarkerStatus(marker, bloodTestResult, selectedBloodPatient)">
              {{ formatValue(bloodTestResult[marker.key]) }}
              <span class="blood-unit">{{ marker.unit }}</span>
            </div>
            <div class="blood-range">ref: {{ marker.refRange(selectedBloodPatient) }}</div>
            <div class="blood-status-badge" :class="getMarkerStatus(marker, bloodTestResult, selectedBloodPatient)">
              {{ getMarkerLabel(marker, bloodTestResult, selectedBloodPatient) }}
            </div>
          </div>
        </div>

        <div v-else class="empty">Generating result…</div>

        <div class="blood-meta" v-if="bloodTestResult">
          <span>Taken at: <strong>{{ formatDateTime(bloodTestResult.takenAt) }}</strong></span>
          <button class="btn-accent" @click="generateBloodTest(selectedBloodPatient)" :disabled="bloodTestLoadingId === selectedBloodPatient.id">
            {{ bloodTestLoadingId === selectedBloodPatient.id ? 'Generating…' : '↺ Regenerate' }}
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import MealRecommendations from './MealRecommendations.vue'
import AlimentManager from './AlimentManager.vue'

const healthStatus = ref('Checking…')
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

const doctors = ref([])
const doctorCount = ref(5)
const isDoctorGenerating = ref(false)
const isDoctorLoading = ref(false)
const doctorGenerationMessage = ref('')
const doctorErrorDetails = ref('')

const selectedBloodPatient = ref(null)
const bloodTestResult = ref(null)
const bloodTestLoadingId = ref(null)
const bloodPanel = ref(null)

const bloodMarkers = [
  {
    key: 'hemoglobinGdl',
    label: 'Hemoglobin (Hb)',
    unit: 'g/dL',
    refRange: (p) => p?.sex === 'MALE' ? '13.8–17.2' : '12.1–15.1',
    low: (p) => p?.sex === 'MALE' ? 13.8 : 12.1,
    high: (p) => p?.sex === 'MALE' ? 17.2 : 15.1,
  },
  {
    key: 'wbcThousandsPerUl',
    label: 'White Blood Cells',
    unit: '×10³/µL',
    refRange: () => '4.5–11.0',
    low: () => 4.5,
    high: () => 11.0,
  },
  {
    key: 'plateletsThousandsPerUl',
    label: 'Platelets',
    unit: '×10³/µL',
    refRange: () => '150–450',
    low: () => 150,
    high: () => 450,
  },
  {
    key: 'crpMgL',
    label: 'CRP',
    unit: 'mg/L',
    refRange: () => '< 10',
    low: () => null,
    high: () => 10,
  },
  {
    key: 'creatinineMgdl',
    label: 'Creatinine',
    unit: 'mg/dL',
    refRange: (p) => p?.sex === 'MALE' ? '0.74–1.35' : '0.59–1.04',
    low: (p) => p?.sex === 'MALE' ? 0.74 : 0.59,
    high: (p) => p?.sex === 'MALE' ? 1.35 : 1.04,
  },
  {
    key: 'astUL',
    label: 'AST',
    unit: 'U/L',
    refRange: () => '8–40',
    low: () => 8,
    high: () => 40,
  },
  {
    key: 'altUL',
    label: 'ALT',
    unit: 'U/L',
    refRange: () => '8–40',
    low: () => 8,
    high: () => 40,
  },
  {
    key: 'ggtUL',
    label: 'GGT',
    unit: 'U/L',
    refRange: () => '9–48',
    low: () => 9,
    high: () => 48,
  },
  {
    key: 'sodiumMeqL',
    label: 'Sodium (Na)',
    unit: 'mEq/L',
    refRange: () => '135–145',
    low: () => 135,
    high: () => 145,
  },
  {
    key: 'potassiumMeqL',
    label: 'Potassium (K)',
    unit: 'mEq/L',
    refRange: () => '3.5–5.0',
    low: () => 3.5,
    high: () => 5.0,
  },
  {
    key: 'fastingGlucoseMgdl',
    label: 'Fasting Glucose',
    unit: 'mg/dL',
    refRange: () => '70–99',
    low: () => 70,
    high: () => 99,
  },
  {
    key: 'totalCholesterolMgdl',
    label: 'Total Cholesterol',
    unit: 'mg/dL',
    refRange: () => '< 200',
    low: () => null,
    high: () => 200,
  },
]

function getMarkerStatus(marker, result, patient) {
  const val = result[marker.key]
  if (val == null) return 'normal'
  const lo = marker.low(patient)
  const hi = marker.high(patient)
  if (hi != null && val > hi) return 'high'
  if (lo != null && val < lo) return 'low'
  return 'normal'
}

function getMarkerLabel(marker, result, patient) {
  const status = getMarkerStatus(marker, result, patient)
  if (status === 'high') return '↑ High'
  if (status === 'low') return '↓ Low'
  return '✓ Normal'
}

function formatValue(v) {
  if (v == null) return 'N/A'
  return Number(v).toFixed(2)
}

function formatDateTime(dt) {
  if (!dt) return 'N/A'
  return new Date(dt).toLocaleString()
}

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
    await loadDoctors()
  }
})

async function handleLogin() {
  isLoggingIn.value = true
  loginError.value = ''
  try {
    const res = await fetch('/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value.trim(), password: password.value.trim() })
    })
    const data = await res.json()
    if (data.success && data.token) {
      isLoggedIn.value = true
      jwtToken.value = data.token
      username.value = data.username
      localStorage.setItem('jwt_token', data.token)
      localStorage.setItem('username', data.username)
      await loadPatients()
      await loadDoctors()
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
  doctors.value = []
  generationMessage.value = ''
  errorDetails.value = ''
  doctorGenerationMessage.value = ''
  doctorErrorDetails.value = ''
  selectedPatient.value = null
  selectedBloodPatient.value = null
  bloodTestResult.value = null
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
  generationMessage.value = 'Generating patients…'
  errorDetails.value = ''
  try {
    const endpoint = patientCount.value === 1 ? '/patients/mock' : `/patients/mock/${patientCount.value}`
    const res = await fetch(endpoint, { method: 'POST', headers: getAuthHeaders() })
    if (res.ok) {
      generationMessage.value = `Successfully generated ${patientCount.value} patient(s)!`
      await loadPatients()
    } else if (res.status === 401 || res.status === 403) {
      generationMessage.value = 'Authentication failed.'
      logout()
    } else {
      errorDetails.value = `Status: ${res.status}\n${await res.text()}`
      generationMessage.value = 'Error generating patients'
    }
  } catch (error) {
    generationMessage.value = 'Error generating patients'
    errorDetails.value = error.message
  } finally {
    isGenerating.value = false
  }
}

async function loadPatients() {
  isLoading.value = true
  generationMessage.value = ''
  errorDetails.value = ''
  try {
    const res = await fetch('/patients', { headers: getAuthHeaders() })
    if (res.ok) {
      patients.value = await res.json()
    } else if (res.status === 401 || res.status === 403) {
      logout()
    } else {
      errorDetails.value = `Status: ${res.status}\n${await res.text()}`
      generationMessage.value = 'Error loading patients'
    }
  } catch (error) {
    generationMessage.value = 'Error loading patients'
    errorDetails.value = error.message
  } finally {
    isLoading.value = false
  }
}

async function generateDoctors() {
  isDoctorGenerating.value = true
  doctorGenerationMessage.value = 'Generating doctors…'
  doctorErrorDetails.value = ''
  try {
    const endpoint = doctorCount.value === 1 ? '/doctors/mock' : `/doctors/mock/${doctorCount.value}`
    const res = await fetch(endpoint, { method: 'POST', headers: getAuthHeaders() })
    if (res.ok) {
      doctorGenerationMessage.value = `Successfully generated ${doctorCount.value} doctor(s)!`
      await loadDoctors()
    } else if (res.status === 401 || res.status === 403) {
      logout()
    } else {
      doctorErrorDetails.value = `Status: ${res.status}\n${await res.text()}`
      doctorGenerationMessage.value = 'Error generating doctors'
    }
  } catch (error) {
    doctorGenerationMessage.value = 'Error generating doctors'
    doctorErrorDetails.value = error.message
  } finally {
    isDoctorGenerating.value = false
  }
}

async function loadDoctors() {
  isDoctorLoading.value = true
  doctorGenerationMessage.value = ''
  doctorErrorDetails.value = ''
  try {
    const res = await fetch('/doctors', { headers: getAuthHeaders() })
    if (res.ok) {
      doctors.value = await res.json()
    } else if (res.status === 401 || res.status === 403) {
      logout()
    } else {
      doctorErrorDetails.value = `Status: ${res.status}\n${await res.text()}`
      doctorGenerationMessage.value = 'Error loading doctors'
    }
  } catch (error) {
    doctorGenerationMessage.value = 'Error loading doctors'
    doctorErrorDetails.value = error.message
  } finally {
    isDoctorLoading.value = false
  }
}

async function generateBloodTest(patient) {
  bloodTestLoadingId.value = patient.id
  selectedBloodPatient.value = patient
  bloodTestResult.value = null
  try {
    const res = await fetch(`/blood-tests/mock/patient/${patient.id}`, {
      method: 'POST',
      headers: getAuthHeaders()
    })
    if (res.ok) {
      bloodTestResult.value = await res.json()
      await nextTick()
      bloodPanel.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    } else {
      bloodTestResult.value = null
    }
  } catch (error) {
    bloodTestResult.value = null
  } finally {
    bloodTestLoadingId.value = null
  }
}

function viewMealPlan(patient) {
  selectedPatient.value = patient
  nextTick(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }))
}

function calculateBMI(patient) {
  if (!patient.heightCm || !patient.weightKg) return 0
  const h = patient.heightCm / 100
  return patient.weightKg / (h * h)
}

function calculateAge(birthDate) {
  if (!birthDate) return 'N/A'
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const m = today.getMonth() - birth.getMonth()
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
  return age
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@400;600&family=IBM+Plex+Sans:wght@400;500;600&display=swap');

* { box-sizing: border-box; margin: 0; padding: 0; }

.app {
  font-family: 'IBM Plex Sans', sans-serif;
  background: #0d0f14;
  color: #c8cdd8;
  min-height: 100vh;
  font-size: 13px;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 28px;
  border-bottom: 1px solid #1e2330;
  background: #0d0f14;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-family: 'IBM Plex Mono', monospace;
  font-weight: 600;
  font-size: 15px;
  color: #e2e8f0;
  letter-spacing: 0.04em;
}

.backend-status {
  font-family: 'IBM Plex Mono', monospace;
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.backend-status.online { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.backend-status.offline { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }

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

.main-content {
  padding: 28px 28px 60px;
  max-width: 1800px;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 14px;
  margin-bottom: 24px;
}

.welcome {
  font-family: 'IBM Plex Mono', monospace;
  font-size: 12px;
  color: #64748b;
}

.section {
  background: #13161f;
  border: 1px solid #1e2330;
  border-radius: 10px;
  margin-bottom: 20px;
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #1e2330;
}

.section-header h2 {
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
  letter-spacing: 0.03em;
}

.controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.count-input {
  width: 68px;
  background: #0d0f14;
  border: 1px solid #2a3045;
  border-radius: 5px;
  color: #c8cdd8;
  padding: 6px 10px;
  font-family: 'IBM Plex Mono', monospace;
  font-size: 12px;
  outline: none;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

thead tr {
  background: #0f1219;
}

th {
  padding: 10px 14px;
  text-align: left;
  color: #4b5563;
  font-weight: 600;
  font-family: 'IBM Plex Mono', monospace;
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  border-bottom: 1px solid #1e2330;
  white-space: nowrap;
}

td {
  padding: 9px 14px;
  border-bottom: 1px solid #161a24;
  color: #9ca3af;
  white-space: nowrap;
}

tr:last-child td { border-bottom: none; }

tr:hover td { background: #161a24; }

tr.row-active td { background: #0f1a2e; }

.mono {
  font-family: 'IBM Plex Mono', monospace;
  color: #4b5563;
}

.tag {
  display: inline-block;
  padding: 2px 7px;
  background: #1a2035;
  border: 1px solid #2a3045;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  color: #60a5fa;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.empty {
  padding: 28px 20px;
  color: #374151;
  font-style: italic;
  text-align: center;
}

.msg {
  margin: 12px 20px;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}
.msg.success { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.msg.error { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.msg pre { font-family: 'IBM Plex Mono', monospace; font-size: 11px; white-space: pre-wrap; }

.btn-primary {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 7px 16px;
  font-family: 'IBM Plex Sans', sans-serif;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { opacity: 0.4; cursor: not-allowed; }

.btn-ghost {
  background: transparent;
  color: #6b7280;
  border: 1px solid #2a3045;
  border-radius: 6px;
  padding: 7px 14px;
  font-family: 'IBM Plex Sans', sans-serif;
  font-size: 12px;
  cursor: pointer;
  transition: color 0.15s, border-color 0.15s;
}
.btn-ghost:hover:not(:disabled) { color: #c8cdd8; border-color: #4b5563; }
.btn-ghost:disabled { opacity: 0.4; cursor: not-allowed; }

.btn-ghost-sm {
  background: transparent;
  color: #6b7280;
  border: 1px solid #2a3045;
  border-radius: 5px;
  padding: 4px 10px;
  font-size: 11px;
  cursor: pointer;
  transition: color 0.15s, border-color 0.15s;
  white-space: nowrap;
}
.btn-ghost-sm:hover { color: #c8cdd8; border-color: #4b5563; }

.btn-accent {
  background: #1a1030;
  color: #a78bfa;
  border: 1px solid #4c1d95;
  border-radius: 5px;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  white-space: nowrap;
}
.btn-accent:hover:not(:disabled) { background: #2e1065; border-color: #7c3aed; color: #c4b5fd; }
.btn-accent:disabled { opacity: 0.4; cursor: not-allowed; }

.panel {
  background: #13161f;
  border: 1px solid #1e2330;
  border-radius: 10px;
  margin-top: 20px;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #1e2330;
}

.panel-header h3 {
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
}

.blood-panel {
  border-color: #2e1a4a;
}

.blood-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1px;
  background: #1e2330;
  border-top: none;
}

.blood-card {
  background: #13161f;
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.blood-label {
  font-size: 10px;
  font-weight: 600;
  color: #4b5563;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  font-family: 'IBM Plex Mono', monospace;
}

.blood-value {
  font-family: 'IBM Plex Mono', monospace;
  font-size: 22px;
  font-weight: 600;
  line-height: 1;
  color: #e2e8f0;
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.blood-value.high { color: #f87171; }
.blood-value.low  { color: #60a5fa; }
.blood-value.normal { color: #34d399; }

.blood-unit {
  font-size: 10px;
  color: #4b5563;
  font-weight: 400;
}

.blood-range {
  font-size: 10px;
  color: #374151;
  font-family: 'IBM Plex Mono', monospace;
}

.blood-status-badge {
  display: inline-block;
  margin-top: 4px;
  padding: 2px 7px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  align-self: flex-start;
}
.blood-status-badge.normal { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.blood-status-badge.high   { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.blood-status-badge.low    { background: #0c1a35; color: #60a5fa; border: 1px solid #1e3a6e; }

.blood-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-top: 1px solid #1e2330;
  font-size: 11px;
  color: #4b5563;
  font-family: 'IBM Plex Mono', monospace;
}

.blood-meta strong { color: #9ca3af; }
</style>