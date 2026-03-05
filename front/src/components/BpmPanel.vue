<template>
  <div v-if="patient" class="panel bpm-panel">
    <div class="panel-header">
      <h3>📈 BPM History — {{ patient.name }}</h3>
      <div class="panel-header-actions">
        <button class="btn-ghost" @click="load">↺ Refresh</button>
        <button class="btn-ghost" @click="$emit('close')">✕ Close</button>
      </div>
    </div>

    <div v-if="history.length > 0" class="bpm-content">
      <div class="bpm-stats">
        <div class="bpm-stat">
          <div class="bpm-stat-label">Latest</div>
          <div class="bpm-stat-value" :class="statusClass(history[0].value)">
            {{ history[0].value }} <span class="bpm-unit">bpm</span>
          </div>
        </div>
        <div class="bpm-stat">
          <div class="bpm-stat-label">Average</div>
          <div class="bpm-stat-value">{{ avg }} <span class="bpm-unit">bpm</span></div>
        </div>
        <div class="bpm-stat">
          <div class="bpm-stat-label">Min</div>
          <div class="bpm-stat-value low">{{ min }} <span class="bpm-unit">bpm</span></div>
        </div>
        <div class="bpm-stat">
          <div class="bpm-stat-label">Max</div>
          <div class="bpm-stat-value" :class="max > patient.bpmMax ? 'high' : 'normal'">
            {{ max }} <span class="bpm-unit">bpm</span>
          </div>
        </div>
        <div class="bpm-stat">
          <div class="bpm-stat-label">Max Allowed</div>
          <div class="bpm-stat-value">{{ patient.bpmMax }} <span class="bpm-unit">bpm</span></div>
        </div>
        <div class="bpm-stat">
          <div class="bpm-stat-label">Records</div>
          <div class="bpm-stat-value">{{ history.length }}</div>
        </div>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>#</th><th>Timestamp</th><th>BPM Value</th><th>Status</th></tr>
          </thead>
          <tbody>
            <tr v-for="(entry, i) in history.slice(0, 50)" :key="entry.id">
              <td class="mono">{{ i + 1 }}</td>
              <td class="mono">{{ fmt(entry.timestamp) }}</td>
              <td><span class="bpm-badge" :class="statusClass(entry.value)">{{ entry.value }} bpm</span></td>
              <td><span class="status-badge" :class="statusClass(entry.value)">{{ statusLabel(entry.value) }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="history.length > 50" class="bpm-truncate">
        Showing 50 most recent of {{ history.length }} total records.
      </div>
    </div>

    <div v-else-if="loading" class="empty">Loading BPM history…</div>
    <div v-else class="empty">No BPM records found. Enable tracking and wait for the scheduler to run.</div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { getAuthHeaders } from '../useAuth.js'

const props = defineProps({ patient: { type: Object, default: null } })
const emit = defineEmits(['close'])

const history = ref([])
const loading = ref(false)

const avg = computed(() => {
  if (!history.value.length) return 0
  return Math.round(history.value.reduce((s, e) => s + e.value, 0) / history.value.length)
})
const min = computed(() => history.value.length ? Math.min(...history.value.map(e => e.value)) : 0)
const max = computed(() => history.value.length ? Math.max(...history.value.map(e => e.value)) : 0)

watch(() => props.patient, (p) => { if (p) load() }, { immediate: true })

async function load() {
  if (!props.patient) return
  loading.value = true
  try {
    const res = await fetch(`/bpm/patient/${props.patient.id}`, { headers: getAuthHeaders() })
    if (res.ok) {
      const data = await res.json()
      history.value = data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    }
  } catch (err) {
    history.value = []
  } finally {
    loading.value = false
  }
}

function statusClass(value) {
  if (value > props.patient.bpmMax) return 'high'
  if (value < 40) return 'low'
  return 'normal'
}

function statusLabel(value) {
  if (value > props.patient.bpmMax) return '↑ Exceeded'
  if (value < 40) return '↓ Low'
  return '✓ Normal'
}

function fmt(dt) {
  return dt ? new Date(dt).toLocaleString() : 'N/A'
}
</script>

<style scoped>
.panel { background: #13161f; border: 1px solid #1e2330; border-radius: 10px; margin-top: 20px; overflow: hidden; }
.bpm-panel { border-color: #0c3352; }
.panel-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #1e2330; }
.panel-header h3 { font-size: 14px; font-weight: 600; color: #e2e8f0; }
.panel-header-actions { display: flex; gap: 8px; }
.bpm-stats { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 1px; background: #1e2330; border-bottom: 1px solid #1e2330; }
.bpm-stat { background: #13161f; padding: 16px 18px; display: flex; flex-direction: column; gap: 6px; }
.bpm-stat-label { font-size: 10px; font-weight: 600; color: #4b5563; text-transform: uppercase; letter-spacing: 0.07em; font-family: 'IBM Plex Mono', monospace; }
.bpm-stat-value { font-family: 'IBM Plex Mono', monospace; font-size: 26px; font-weight: 600; color: #e2e8f0; line-height: 1; display: flex; align-items: baseline; gap: 4px; }
.bpm-unit { font-size: 10px; color: #4b5563; font-weight: 400; }
.bpm-stat-value.high { color: #f87171; }
.bpm-stat-value.low { color: #60a5fa; }
.bpm-stat-value.normal { color: #34d399; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 12px; }
thead tr { background: #0f1219; }
th { padding: 10px 14px; text-align: left; color: #4b5563; font-weight: 600; font-family: 'IBM Plex Mono', monospace; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; border-bottom: 1px solid #1e2330; white-space: nowrap; }
td { padding: 9px 14px; border-bottom: 1px solid #161a24; color: #9ca3af; white-space: nowrap; }
tr:last-child td { border-bottom: none; }
.mono { font-family: 'IBM Plex Mono', monospace; color: #4b5563; }
.bpm-badge { font-family: 'IBM Plex Mono', monospace; font-size: 12px; font-weight: 600; }
.bpm-badge.high { color: #f87171; }
.bpm-badge.low { color: #60a5fa; }
.bpm-badge.normal { color: #9ca3af; }
.status-badge { display: inline-block; padding: 2px 7px; border-radius: 4px; font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; }
.status-badge.normal { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.status-badge.high { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.status-badge.low { background: #0c1a35; color: #60a5fa; border: 1px solid #1e3a6e; }
.bpm-truncate { padding: 10px 20px; font-size: 11px; color: #4b5563; font-style: italic; border-top: 1px solid #1e2330; font-family: 'IBM Plex Mono', monospace; }
.empty { padding: 28px 20px; color: #374151; font-style: italic; text-align: center; }
.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 7px 14px; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover { color: #c8cdd8; border-color: #4b5563; }
</style>