<template>
  <section class="section">
    <div class="section-header">
      <h2>Doctors</h2>
      <div class="controls">
        <input v-model.number="doctorCount" type="number" min="1" max="100" class="count-input" />
        <button class="btn-primary" @click="generateDoctors" :disabled="isGenerating">
          {{ isGenerating ? 'Generating…' : 'Generate' }}
        </button>
        <button class="btn-ghost" @click="loadDoctors" :disabled="isLoading">
          {{ isLoading ? '…' : '↺ Refresh' }}
        </button>
      </div>
    </div>

    <div v-if="message" class="msg" :class="hasError ? 'error' : 'success'">{{ message }}</div>

    <div v-if="doctors.length > 0" class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Sex</th><th>Specialty</th><th>Experience (yrs)</th><th>Hospital</th>
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
    <div v-else-if="!isLoading" class="empty">No doctors yet.</div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAuthHeaders } from '../useAuth.js'

const doctors = ref([])
const doctorCount = ref(5)
const isGenerating = ref(false)
const isLoading = ref(false)
const message = ref('')
const hasError = ref(false)

onMounted(loadDoctors)

async function generateDoctors() {
  isGenerating.value = true
  message.value = 'Generating doctors…'
  hasError.value = false
  try {
    const endpoint = doctorCount.value === 1 ? '/doctors/mock' : `/doctors/mock/${doctorCount.value}`
    const res = await fetch(endpoint, { method: 'POST', headers: getAuthHeaders() })
    if (res.ok) {
      message.value = `Generated ${doctorCount.value} doctor(s)!`
      await loadDoctors()
    } else {
      hasError.value = true
      message.value = 'Error generating doctors'
    }
  } catch (err) {
    hasError.value = true
    message.value = 'Error generating doctors'
  } finally {
    isGenerating.value = false
  }
}

async function loadDoctors() {
  isLoading.value = true
  message.value = ''
  hasError.value = false
  try {
    const res = await fetch('/doctors', { headers: getAuthHeaders() })
    if (res.ok) doctors.value = await res.json()
  } catch (err) {
    message.value = 'Error loading doctors'
    hasError.value = true
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.section { background: #13161f; border: 1px solid #1e2330; border-radius: 10px; margin-bottom: 20px; overflow: hidden; }
.section-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #1e2330; }
.section-header h2 { font-size: 14px; font-weight: 600; color: #e2e8f0; }
.controls { display: flex; align-items: center; gap: 8px; }
.count-input { width: 68px; background: #0d0f14; border: 1px solid #2a3045; border-radius: 5px; color: #c8cdd8; padding: 6px 10px; font-family: 'IBM Plex Mono', monospace; font-size: 12px; outline: none; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 12px; }
thead tr { background: #0f1219; }
th { padding: 10px 14px; text-align: left; color: #4b5563; font-weight: 600; font-family: 'IBM Plex Mono', monospace; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; border-bottom: 1px solid #1e2330; white-space: nowrap; }
td { padding: 9px 14px; border-bottom: 1px solid #161a24; color: #9ca3af; white-space: nowrap; }
tr:last-child td { border-bottom: none; }
tr:hover td { background: #161a24; }
.mono { font-family: 'IBM Plex Mono', monospace; color: #4b5563; }
.empty { padding: 28px 20px; color: #374151; font-style: italic; text-align: center; }
.msg { margin: 12px 20px; padding: 8px 14px; border-radius: 6px; font-size: 12px; font-weight: 500; }
.msg.success { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.msg.error { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.btn-primary { background: #2563eb; color: #fff; border: none; border-radius: 6px; padding: 7px 16px; font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s; }
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 7px 14px; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover:not(:disabled) { color: #c8cdd8; border-color: #4b5563; }
.btn-ghost:disabled { opacity: 0.4; cursor: not-allowed; }
</style>