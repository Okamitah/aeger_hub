<template>
  <div class="dashboard">
    <header class="dash-header">
      <div class="header-left">
        <span class="logo-hex">⬡</span>
        <span class="logo-text">AegerHub</span>
        <span class="role-badge admin">🔐 Admin</span>
      </div>
      <div class="header-right">
        <span class="dash-user">{{ username }}</span>
        <button class="btn-ghost" @click="handleLogout">Sign Out</button>
      </div>
    </header>
    <main class="dash-body">
      <p class="dash-label">Admin Dashboard — coming soon</p>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { username, logout } from '../useAuth.js'

const router = useRouter()

function handleLogout() {
  logout()
  router.push({ name: 'landing' })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Syne:wght@700;800&family=DM+Sans:wght@400;500&display=swap');

.dashboard { display: flex; flex-direction: column; min-height: 100vh; background: #070b12; font-family: 'DM Sans', sans-serif; }

.dash-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 28px; border-bottom: 1px solid rgba(255,255,255,0.05);
  background: rgba(7,11,18,0.95); backdrop-filter: blur(12px);
  position: sticky; top: 0; z-index: 100;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.logo-hex { font-size: 20px; color: #38bdf8; filter: drop-shadow(0 0 6px rgba(56,189,248,0.5)); }
.logo-text { font-family: 'Syne', sans-serif; font-size: 16px; font-weight: 800; color: #e8edf5; letter-spacing: -0.02em; }
.role-badge { font-size: 11px; font-weight: 500; padding: 3px 10px; border-radius: 20px; border: 1px solid; }
.role-badge.admin { background: rgba(239,68,68,0.08); border-color: rgba(239,68,68,0.3); color: #f87171; }
.header-right { display: flex; align-items: center; gap: 14px; }
.dash-user { font-size: 11px; color: #3a4555; font-family: monospace; }
.btn-ghost { background: transparent; color: #6b7280; border: 1px solid #2a3045; border-radius: 6px; padding: 6px 14px; font-size: 12px; cursor: pointer; transition: color 0.15s, border-color 0.15s; }
.btn-ghost:hover { color: #c8cdd8; border-color: #4b5563; }

.dash-body { flex: 1; display: flex; align-items: center; justify-content: center; }
.dash-label { font-family: 'Syne', sans-serif; font-size: 20px; font-weight: 700; color: #1e2535; }
</style>