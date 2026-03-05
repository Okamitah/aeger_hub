<template>
  <div class="app">
    <header class="app-header">
      <span class="logo">⬡ AegerHub</span>
      <span class="backend-status" :class="healthStatus === 'Offline' ? 'offline' : 'online'">
        {{ healthStatus }}
      </span>
    </header>

    <LoginView v-if="!isLoggedIn" @login-success="onLoginSuccess" />

    <div v-else class="main-content">
      <div class="top-bar">
        <span class="welcome">{{ username }}</span>
        <button class="btn-ghost" @click="handleLogout">Logout</button>
      </div>

      <PatientTable
        :active-patient-id="activePanelPatientId"
        :blood-loading-id="bloodLoadingId"
        @view-bpm="onViewBpm"
        @generate-blood-test="onGenerateBloodTest"
        @view-meal-plan="onViewMealPlan"
      />

      <DoctorTable />

      <AlimentManager :token="jwtToken" />

      <!-- MEAL PLAN -->
      <div v-if="mealPatient" class="panel">
        <div class="panel-header">
          <h3>Meal Plan — {{ mealPatient.name }}</h3>
          <button class="btn-ghost" @click="mealPatient = null">✕ Close</button>
        </div>
        <p class="panel-sub">Condition: <strong>{{ mealPatient.illness }}</strong></p>
        <MealRecommendations v-if="mealPatient.illness" :illness="mealPatient.illness" :token="jwtToken" />
      </div>

      <BpmPanel :patient="bpmPatient" @close="bpmPatient = null" ref="bpmPanelRef" />

      <BloodTestPanel
        :patient="bloodPatient"
        :result="bloodResult"
        :loading="!!bloodLoadingId"
        @close="bloodPatient = null; bloodResult = null"
        @regenerate="onGenerateBloodTest"
        ref="bloodPanelRef"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { isLoggedIn, jwtToken, username, logout, restoreSession, getAuthHeaders } from './useAuth.js'
import LoginView from './components/LoginView.vue'
import PatientTable from './components/PatientTable.vue'
import DoctorTable from './components/DoctorTable.vue'
import BpmPanel from './components/BpmPanel.vue'
import BloodTestPanel from './components/BloodTestPanel.vue'
import AlimentManager from './AlimentManager.vue'
import MealRecommendations from './MealRecommendations.vue'

const healthStatus = ref('Checking…')

const mealPatient = ref(null)
const bpmPatient = ref(null)
const bpmPanelRef = ref(null)

const bloodPatient = ref(null)
const bloodResult = ref(null)
const bloodLoadingId = ref(null)
const bloodPanelRef = ref(null)

const activePanelPatientId = ref(null)

onMounted(async () => {
  restoreSession()
  try {
    const res = await fetch('/api/')
    if (res.ok) {
      const data = await res.json()
      healthStatus.value = data.message || 'BACKEND IS RUNNING WITH SPRING BOOT'
    } else {
      throw new Error()
    }
  } catch {
    healthStatus.value = 'Offline'
  }
})

function onLoginSuccess() {}

function handleLogout() {
  logout()
  mealPatient.value = null
  bpmPatient.value = null
  bloodPatient.value = null
  bloodResult.value = null
  activePanelPatientId.value = null
}

function onViewBpm(patient) {
  bpmPatient.value = patient
  activePanelPatientId.value = patient.id
  nextTick(() => bpmPanelRef.value?.$el?.scrollIntoView({ behavior: 'smooth', block: 'start' }))
}

async function onGenerateBloodTest(patient) {
  bloodLoadingId.value = patient.id
  bloodPatient.value = patient
  bloodResult.value = null
  activePanelPatientId.value = patient.id
  try {
    const res = await fetch(`/blood-tests/mock/patient/${patient.id}`, {
      method: 'POST',
      headers: getAuthHeaders()
    })
    if (res.ok) {
      bloodResult.value = await res.json()
      await nextTick()
      bloodPanelRef.value?.$el?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  } catch (err) {
    bloodResult.value = null
  } finally {
    bloodLoadingId.value = null
  }
}

function onViewMealPlan(patient) {
  mealPatient.value = patient
  nextTick(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }))
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@400;600&family=IBM+Plex+Sans:wght@400;500;600&display=swap');

* { box-sizing: border-box; margin: 0; padding: 0; }

.app { font-family: 'IBM Plex Sans', sans-serif; background: #0d0f14; color: #c8cdd8; min-height: 100vh; font-size: 13px; }

.app-header { display: flex; align-items: center; justify-content: space-between; padding: 14px 28px; border-bottom: 1px solid #1e2330; background: #0d0f14; position: sticky; top: 0; z-index: 100; }

.logo { font-family: 'IBM Plex Mono', monospace; font-weight: 600; font-size: 15px; color: #e2e8f0; letter-spacing: 0.04em; }

.backend-status { font-family: 'IBM Plex Mono', monospace; font-size: 11px; padding: 3px 10px; border-radius: 20px; font-weight: 600; letter-spacing: 0.06em; text-transform: uppercase; }
.backend-status.online { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.backend-status.offline { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }

.main-content { padding: 28px 28px 60px; }

.top-bar { display: flex; align-items: center; justify-content: flex-end; gap: 14px; margin-bottom: 24px; }

.welcome { font-family: 'IBM Plex Mono', monospace; font-size: 12px; color: #64748b; }

.panel { background: #13161f; border: 1px solid #1e2330; border-radius: 10px; margin-top: 20px; overflow: hidden; }
.panel-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #1e2330; }
.panel-header h3 { font-size: 14px; font-weight: 600; color: #e2e8f0; }
.panel-sub { padding: 12px 20px; font-size: 12px; color: #6b7280; border-bottom: 1px solid #1e2330; }
.panel-sub strong { color: #c8cdd8; }

.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 7px 14px; font-family: 'IBM Plex Sans', sans-serif; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover { color: #c8cdd8; border-color: #4b5563; }
</style>