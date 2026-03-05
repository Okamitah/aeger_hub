<template>
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

    <div v-if="message" class="msg" :class="hasError ? 'error' : 'success'">{{ message }}</div>
    <div v-if="hasError" class="msg error"><pre>{{ errorDetails }}</pre></div>

    <div v-if="patients.length > 0" class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Birth Date</th><th>Age</th><th>Sex</th>
            <th>Height (cm)</th><th>Weight (kg)</th><th>BMI</th><th>Illness</th>
            <th>Sleep</th><th>Athleticism</th><th>Activity</th><th>Smoker</th>
            <th>Drinker</th><th>BPM Max</th><th>Tracking</th><th>BPM History</th>
            <th>Blood Test</th><th>Meal Plan</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="patient in patients"
            :key="patient.id"
            :class="{ 'row-active': activePatientId === patient.id }"
          >
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
            <td>
              <button
                :class="patient.trackingEnabled ? 'btn-tracking-on' : 'btn-tracking-off'"
                @click="toggleTracking(patient)"
                :disabled="trackingToggleId === patient.id"
              >
                {{ trackingToggleId === patient.id ? '…' : patient.trackingEnabled ? '● ON' : '○ OFF' }}
              </button>
            </td>
            <td>
              <button
                class="btn-bpm"
                @click="$emit('view-bpm', patient)"
                :disabled="!patient.trackingEnabled"
                :title="!patient.trackingEnabled ? 'Tracking not enabled' : ''"
              >
                {{ patient.trackingEnabled ? '📈 View' : '—' }}
              </button>
            </td>
            <td>
              <button
                class="btn-accent"
                @click="$emit('generate-blood-test', patient)"
                :disabled="bloodLoadingId === patient.id"
              >
                {{ bloodLoadingId === patient.id ? '…' : '🩸 Generate' }}
              </button>
            </td>
            <td>
              <button class="btn-ghost-sm" @click="$emit('view-meal-plan', patient)">Meal Plan</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else-if="!isLoading" class="empty">No patients yet. Generate some to get started.</div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAuthHeaders } from '../useAuth.js'

const props = defineProps({
  activePatientId: { type: Number, default: null },
  bloodLoadingId: { type: Number, default: null }
})

const emit = defineEmits(['view-bpm', 'generate-blood-test', 'view-meal-plan'])

const patients = ref([])
const patientCount = ref(5)
const isGenerating = ref(false)
const isLoading = ref(false)
const message = ref('')
const errorDetails = ref('')
const hasError = ref(false)
const trackingToggleId = ref(null)

onMounted(loadPatients)

async function generatePatients() {
  isGenerating.value = true
  message.value = 'Generating patients…'
  hasError.value = false
  errorDetails.value = ''
  try {
    const endpoint = patientCount.value === 1 ? '/patients/mock' : `/patients/mock/${patientCount.value}`
    const res = await fetch(endpoint, { method: 'POST', headers: getAuthHeaders() })
    if (res.ok) {
      message.value = `Generated ${patientCount.value} patient(s)!`
      await loadPatients()
    } else {
      hasError.value = true
      errorDetails.value = `Status: ${res.status}\n${await res.text()}`
      message.value = 'Error generating patients'
    }
  } catch (err) {
    hasError.value = true
    errorDetails.value = err.message
    message.value = 'Error generating patients'
  } finally {
    isGenerating.value = false
  }
}

async function loadPatients() {
  isLoading.value = true
  message.value = ''
  hasError.value = false
  try {
    const res = await fetch('/patients', { headers: getAuthHeaders() })
    if (res.ok) patients.value = await res.json()
  } catch (err) {
    message.value = 'Error loading patients'
    hasError.value = true
    errorDetails.value = err.message
  } finally {
    isLoading.value = false
  }
}

async function toggleTracking(patient) {
  trackingToggleId.value = patient.id
  try {
    const res = await fetch(`/patients/${patient.id}/tracking`, {
      method: 'PUT',
      headers: getAuthHeaders()
    })
    if (res.ok) {
      const updated = await res.json()
      const idx = patients.value.findIndex(p => p.id === updated.id)
      if (idx !== -1) patients.value[idx] = updated
    }
  } catch (err) {
    console.error('Failed to toggle tracking', err)
  } finally {
    trackingToggleId.value = null
  }
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

function calculateBMI(patient) {
  if (!patient.heightCm || !patient.weightKg) return 0
  const h = patient.heightCm / 100
  return patient.weightKg / (h * h)
}
</script>

<style scoped>
.section { background: #13161f; border: 1px solid #1e2330; border-radius: 10px; margin-bottom: 20px; overflow: hidden; }
.section-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #1e2330; }
.section-header h2 { font-size: 14px; font-weight: 600; color: #e2e8f0; letter-spacing: 0.03em; }
.controls { display: flex; align-items: center; gap: 8px; }
.count-input { width: 68px; background: #0d0f14; border: 1px solid #2a3045; border-radius: 5px; color: #c8cdd8; padding: 6px 10px; font-family: 'IBM Plex Mono', monospace; font-size: 12px; outline: none; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 12px; }
thead tr { background: #0f1219; }
th { padding: 10px 14px; text-align: left; color: #4b5563; font-weight: 600; font-family: 'IBM Plex Mono', monospace; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; border-bottom: 1px solid #1e2330; white-space: nowrap; }
td { padding: 9px 14px; border-bottom: 1px solid #161a24; color: #9ca3af; white-space: nowrap; }
tr:last-child td { border-bottom: none; }
tr:hover td { background: #161a24; }
tr.row-active td { background: #0f1a2e; }
.mono { font-family: 'IBM Plex Mono', monospace; color: #4b5563; }
.tag { display: inline-block; padding: 2px 7px; background: #1a2035; border: 1px solid #2a3045; border-radius: 4px; font-size: 10px; font-weight: 600; color: #60a5fa; text-transform: uppercase; letter-spacing: 0.04em; }
.empty { padding: 28px 20px; color: #374151; font-style: italic; text-align: center; }
.msg { margin: 12px 20px; padding: 8px 14px; border-radius: 6px; font-size: 12px; font-weight: 500; }
.msg.success { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.msg.error { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.msg pre { font-family: 'IBM Plex Mono', monospace; font-size: 11px; white-space: pre-wrap; }
.btn-primary { background: #2563eb; color: #fff; border: none; border-radius: 6px; padding: 7px 16px; font-family: 'IBM Plex Sans', sans-serif; font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s; }
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 7px 14px; font-family: 'IBM Plex Sans', sans-serif; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover:not(:disabled) { color: #c8cdd8; border-color: #4b5563; }
.btn-ghost:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-ghost-sm { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 5px; padding: 4px 10px; font-size: 11px; cursor: pointer; transition: color 0.15s, border-color 0.15s; white-space: nowrap; }
.btn-ghost-sm:hover { color: #c8cdd8; border-color: #4b5563; }
.btn-accent { background: #1a1030; color: #a78bfa; border: 1px solid #4c1d95; border-radius: 5px; padding: 4px 10px; font-size: 11px; font-weight: 600; cursor: pointer; transition: background 0.15s; white-space: nowrap; }
.btn-accent:hover:not(:disabled) { background: #2e1065; border-color: #7c3aed; color: #c4b5fd; }
.btn-accent:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-bpm { background: #0d1f2d; color: #38bdf8; border: 1px solid #0c4a6e; border-radius: 5px; padding: 4px 10px; font-size: 11px; font-weight: 600; cursor: pointer; transition: background 0.15s; white-space: nowrap; }
.btn-bpm:hover:not(:disabled) { background: #0c2d42; border-color: #0284c7; color: #7dd3fc; }
.btn-bpm:disabled { opacity: 0.3; cursor: not-allowed; }
.btn-tracking-on { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; border-radius: 5px; padding: 4px 10px; font-size: 11px; font-weight: 700; cursor: pointer; transition: background 0.15s; white-space: nowrap; }
.btn-tracking-on:hover:not(:disabled) { background: #064e35; border-color: #10b981; }
.btn-tracking-on:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-tracking-off { background: #1a1a24; color: #4b5563; border: 1px solid #2a3045; border-radius: 5px; padding: 4px 10px; font-size: 11px; font-weight: 700; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
.btn-tracking-off:hover:not(:disabled) { background: #1e2330; color: #9ca3af; border-color: #4b5563; }
.btn-tracking-off:disabled { opacity: 0.4; cursor: not-allowed; }
</style>