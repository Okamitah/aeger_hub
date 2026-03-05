<template>
  <div v-if="patient" class="panel blood-panel">
    <div class="panel-header">
      <h3>🩸 Blood Test — {{ patient.name }}</h3>
      <button class="btn-ghost" @click="$emit('close')">✕ Close</button>
    </div>

    <div v-if="result" class="blood-grid">
      <div class="blood-card" v-for="marker in markers" :key="marker.key">
        <div class="blood-label">{{ marker.label }}</div>
        <div class="blood-value" :class="status(marker)">
          {{ fmt(result[marker.key]) }}
          <span class="blood-unit">{{ marker.unit }}</span>
        </div>
        <div class="blood-range">ref: {{ marker.refRange(patient) }}</div>
        <div class="blood-status-badge" :class="status(marker)">{{ statusLabel(marker) }}</div>
      </div>
    </div>

    <div v-else-if="loading" class="empty">Generating result…</div>

    <div class="blood-meta" v-if="result">
      <span>Taken at: <strong>{{ fmt(result.takenAt) }}</strong></span>
      <button class="btn-accent" @click="$emit('regenerate', patient)" :disabled="loading">
        {{ loading ? 'Generating…' : '↺ Regenerate' }}
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  patient: { type: Object, default: null },
  result: { type: Object, default: null },
  loading: { type: Boolean, default: false }
})

defineEmits(['close', 'regenerate'])

const markers = [
  { key: 'hemoglobinGdl', label: 'Hemoglobin (Hb)', unit: 'g/dL', refRange: p => p?.sex === 'MALE' ? '13.8–17.2' : '12.1–15.1', low: p => p?.sex === 'MALE' ? 13.8 : 12.1, high: p => p?.sex === 'MALE' ? 17.2 : 15.1 },
  { key: 'wbcThousandsPerUl', label: 'White Blood Cells', unit: '×10³/µL', refRange: () => '4.5–11.0', low: () => 4.5, high: () => 11.0 },
  { key: 'plateletsThousandsPerUl', label: 'Platelets', unit: '×10³/µL', refRange: () => '150–450', low: () => 150, high: () => 450 },
  { key: 'crpMgL', label: 'CRP', unit: 'mg/L', refRange: () => '< 10', low: () => null, high: () => 10 },
  { key: 'creatinineMgdl', label: 'Creatinine', unit: 'mg/dL', refRange: p => p?.sex === 'MALE' ? '0.74–1.35' : '0.59–1.04', low: p => p?.sex === 'MALE' ? 0.74 : 0.59, high: p => p?.sex === 'MALE' ? 1.35 : 1.04 },
  { key: 'astUL', label: 'AST', unit: 'U/L', refRange: () => '8–40', low: () => 8, high: () => 40 },
  { key: 'altUL', label: 'ALT', unit: 'U/L', refRange: () => '8–40', low: () => 8, high: () => 40 },
  { key: 'ggtUL', label: 'GGT', unit: 'U/L', refRange: () => '9–48', low: () => 9, high: () => 48 },
  { key: 'sodiumMeqL', label: 'Sodium (Na)', unit: 'mEq/L', refRange: () => '135–145', low: () => 135, high: () => 145 },
  { key: 'potassiumMeqL', label: 'Potassium (K)', unit: 'mEq/L', refRange: () => '3.5–5.0', low: () => 3.5, high: () => 5.0 },
  { key: 'fastingGlucoseMgdl', label: 'Fasting Glucose', unit: 'mg/dL', refRange: () => '70–99', low: () => 70, high: () => 99 },
  { key: 'totalCholesterolMgdl', label: 'Total Cholesterol', unit: 'mg/dL', refRange: () => '< 200', low: () => null, high: () => 200 },
]

function status(marker) {
  const val = props.result?.[marker.key]
  if (val == null) return 'normal'
  const lo = marker.low(props.patient)
  const hi = marker.high(props.patient)
  if (hi != null && val > hi) return 'high'
  if (lo != null && val < lo) return 'low'
  return 'normal'
}

function statusLabel(marker) {
  const s = status(marker)
  if (s === 'high') return '↑ High'
  if (s === 'low') return '↓ Low'
  return '✓ Normal'
}

function fmt(v) {
  if (v == null) return 'N/A'
  if (typeof v === 'string' || v instanceof Date || String(v).includes('-')) return new Date(v).toLocaleString()
  return Number(v).toFixed(2)
}
</script>

<style scoped>
.panel { background: #13161f; border: 1px solid #1e2330; border-radius: 10px; margin-top: 20px; overflow: hidden; }
.blood-panel { border-color: #2e1a4a; }
.panel-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #1e2330; }
.panel-header h3 { font-size: 14px; font-weight: 600; color: #e2e8f0; }
.blood-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 1px; background: #1e2330; }
.blood-card { background: #13161f; padding: 16px 18px; display: flex; flex-direction: column; gap: 5px; }
.blood-label { font-size: 10px; font-weight: 600; color: #4b5563; text-transform: uppercase; letter-spacing: 0.07em; font-family: 'IBM Plex Mono', monospace; }
.blood-value { font-family: 'IBM Plex Mono', monospace; font-size: 22px; font-weight: 600; line-height: 1; color: #e2e8f0; display: flex; align-items: baseline; gap: 4px; }
.blood-value.high { color: #f87171; }
.blood-value.low { color: #60a5fa; }
.blood-value.normal { color: #34d399; }
.blood-unit { font-size: 10px; color: #4b5563; font-weight: 400; }
.blood-range { font-size: 10px; color: #374151; font-family: 'IBM Plex Mono', monospace; }
.blood-status-badge { display: inline-block; margin-top: 4px; padding: 2px 7px; border-radius: 4px; font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; align-self: flex-start; }
.blood-status-badge.normal { background: #0d2b1f; color: #34d399; border: 1px solid #065f46; }
.blood-status-badge.high { background: #2d1010; color: #f87171; border: 1px solid #7f1d1d; }
.blood-status-badge.low { background: #0c1a35; color: #60a5fa; border: 1px solid #1e3a6e; }
.blood-meta { display: flex; align-items: center; justify-content: space-between; padding: 14px 20px; border-top: 1px solid #1e2330; font-size: 11px; color: #4b5563; font-family: 'IBM Plex Mono', monospace; }
.blood-meta strong { color: #9ca3af; }
.empty { padding: 28px 20px; color: #374151; font-style: italic; text-align: center; }
.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 7px 14px; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover { color: #c8cdd8; border-color: #4b5563; }
.btn-accent { background: #1a1030; color: #a78bfa; border: 1px solid #4c1d95; border-radius: 5px; padding: 4px 10px; font-size: 11px; font-weight: 600; cursor: pointer; transition: background 0.15s; white-space: nowrap; }
.btn-accent:hover:not(:disabled) { background: #2e1065; border-color: #7c3aed; color: #c4b5fd; }
.btn-accent:disabled { opacity: 0.4; cursor: not-allowed; }
</style>