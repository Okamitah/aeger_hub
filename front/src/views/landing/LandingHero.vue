<template>
  <div class="hero-left">
    <div class="hero-eyebrow">
      <span class="eyebrow-dot" :class="portal"></span>
      {{ portal === 'patient' ? 'Patient Access' : 'Medical Professional' }}
    </div>

    <h1 class="hero-title">
      <template v-if="portal === 'patient'">
        Your health,<br /><em>connected.</em>
      </template>
      <template v-else>
        Patient care,<br /><em>elevated.</em>
      </template>
    </h1>

    <p class="hero-sub">
      <template v-if="portal === 'patient'">
        Access your health records, meal plans, BPM history and blood test results — all in one secure place.
      </template>
      <template v-else>
        Monitor patients in real-time, review diagnostics, manage meal recommendations and respond to alerts.
      </template>
    </p>

    <div class="hero-stats">
      <div class="stat">
        <span class="stat-num">2 500+</span>
        <span class="stat-label">Registered doctors</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat">
        <span class="stat-num">Real-time</span>
        <span class="stat-label">BPM monitoring</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat">
        <span class="stat-num">100%</span>
        <span class="stat-label">Secure & encrypted</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ portal: { type: String, required: true } })
</script>

<style scoped>
.hero-left { padding-top: 20px; }

.hero-eyebrow {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; font-weight: 500; letter-spacing: 0.12em;
  text-transform: uppercase; color: #4a5568; margin-bottom: 24px;
}
.eyebrow-dot { width: 6px; height: 6px; border-radius: 50%; animation: pulse 2s infinite; }
.eyebrow-dot.patient { background: #38bdf8; }
.eyebrow-dot.doctor  { background: #818cf8; }
@keyframes pulse {
  0%   { box-shadow: 0 0 0 0 rgba(56,189,248,0.5); }
  70%  { box-shadow: 0 0 0 8px rgba(56,189,248,0); }
  100% { box-shadow: 0 0 0 0 rgba(56,189,248,0); }
}

.hero-title {
  font-family: 'Syne', sans-serif;
  font-size: clamp(42px, 5vw, 64px); font-weight: 800;
  line-height: 1.1; letter-spacing: -0.03em;
  color: #e8edf5; margin-bottom: 24px;
}
.hero-title em {
  font-style: normal;
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}

.hero-sub {
  font-size: 16px; font-weight: 300; line-height: 1.7;
  color: #4a5568; max-width: 480px; margin-bottom: 48px;
}

.hero-stats { display: flex; align-items: center; gap: 28px; }
.stat { display: flex; flex-direction: column; gap: 4px; }
.stat-num { font-family: 'Syne', sans-serif; font-size: 20px; font-weight: 700; color: #c8d3e0; }
.stat-label { font-size: 11px; color: #3a4555; text-transform: uppercase; letter-spacing: 0.08em; }
.stat-divider { width: 1px; height: 36px; background: rgba(255,255,255,0.06); }

@media (max-width: 900px) {
  .hero-stats { flex-wrap: wrap; gap: 20px; }
  .stat-divider { display: none; }
}
</style>