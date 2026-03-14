<template>
  <nav class="nav">
    <div class="nav-logo">
      <span class="logo-hex">⬡</span>
      <span class="logo-text">AegerHub</span>
    </div>
    <div class="nav-links">
      <button class="nav-tab" :class="{ active: portal === 'patient' }" @click="$emit('switch', 'patient')">
        Patient Portal
      </button>
      <button class="nav-tab" :class="{ active: portal === 'doctor' }" @click="$emit('switch', 'doctor')">
        Doctor Portal
      </button>
    </div>
    <div class="nav-right">
      <span class="nav-badge" :class="portal">
        {{ portal === 'patient' ? '🩺 Patient' : '👨‍⚕️ Doctor' }}
      </span>
    </div>
  </nav>
</template>

<script setup>
defineProps({ portal: { type: String, required: true } })
defineEmits(['switch'])
</script>

<style scoped>
.nav {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 48px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  backdrop-filter: blur(12px);
  background: rgba(7,11,18,0.8);
  position: sticky; top: 0; z-index: 200;
}
.nav-logo { display: flex; align-items: center; gap: 10px; user-select: none; }
.logo-hex {
  font-size: 22px; color: #38bdf8;
  filter: drop-shadow(0 0 8px rgba(56,189,248,0.5));
  transition: filter 0.2s;
}
.logo-text {
  font-family: 'Syne', sans-serif; font-weight: 800; font-size: 18px;
  color: #e8edf5; letter-spacing: -0.02em;
}
.nav-links {
  display: flex; gap: 4px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 10px; padding: 4px;
}
.nav-tab {
  background: none; border: none; color: #5a6478;
  font-family: 'DM Sans', sans-serif; font-size: 13px; font-weight: 500;
  padding: 7px 18px; border-radius: 7px; cursor: pointer; transition: all 0.2s;
}
.nav-tab.active { background: rgba(56,189,248,0.12); color: #38bdf8; }
.nav-tab:hover:not(.active) { color: #8899b0; }
.nav-badge {
  font-size: 12px; font-weight: 500; padding: 5px 12px;
  border-radius: 20px; border: 1px solid; transition: all 0.3s;
}
.nav-badge.patient { background: rgba(56,189,248,0.08); border-color: rgba(56,189,248,0.3); color: #38bdf8; }
.nav-badge.doctor  { background: rgba(99,102,241,0.08); border-color: rgba(99,102,241,0.3); color: #818cf8; }

@media (max-width: 900px) { .nav { padding: 14px 24px; } }
</style>