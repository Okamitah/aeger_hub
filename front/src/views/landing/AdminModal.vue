<template>
  <transition name="fade">
    <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
      <div class="admin-modal">
        <div class="admin-modal-header">
          <span class="admin-icon">🔐</span>
          <h3>Administrator Access</h3>
          <button class="close-btn" @click="$emit('close')">✕</button>
        </div>
        <div class="admin-modal-body">
          <div class="field">
            <label>Admin Username</label>
            <input
              :value="adminUsername"
              @input="$emit('update:adminUsername', $event.target.value)"
              type="text"
              placeholder="admin"
              @keyup.enter="$emit('login')"
              autocomplete="off"
            />
          </div>
          <div class="field">
            <label>Admin Password</label>
            <input
              :value="adminPassword"
              @input="$emit('update:adminPassword', $event.target.value)"
              type="password"
              placeholder="••••••••"
              @keyup.enter="$emit('login')"
              autocomplete="off"
            />
          </div>
          <div v-if="adminError" class="msg error">{{ adminError }}</div>
          <button class="btn-admin" :disabled="isLoading" @click="$emit('login')">
            <span v-if="isLoading" class="spinner"></span>
            {{ isLoading ? 'Authenticating…' : 'Access Dashboard' }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  visible:       { type: Boolean, required: true },
  adminUsername: { type: String,  required: true },
  adminPassword: { type: String,  required: true },
  adminError:    { type: String,  required: true },
  isLoading:     { type: Boolean, required: true },
})
defineEmits(['close', 'login', 'update:adminUsername', 'update:adminPassword'])
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.7);
  backdrop-filter: blur(6px); display: flex;
  align-items: center; justify-content: center; z-index: 999;
}
.admin-modal {
  background: #0d1220; border: 1px solid rgba(239,68,68,0.2);
  border-radius: 16px; width: 360px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.6), 0 0 0 1px rgba(239,68,68,0.1); overflow: hidden;
}
.admin-modal-header {
  display: flex; align-items: center; gap: 10px; padding: 18px 20px;
  border-bottom: 1px solid rgba(239,68,68,0.1); background: rgba(239,68,68,0.04);
}
.admin-icon { font-size: 18px; }
.admin-modal-header h3 { font-family: 'Syne', sans-serif; font-size: 15px; font-weight: 700; color: #fca5a5; flex: 1; }
.close-btn { background: none; border: none; color: #4a5568; font-size: 14px; cursor: pointer; padding: 4px; line-height: 1; }
.close-btn:hover { color: #9ca3af; }
.admin-modal-body { padding: 20px; }

.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.field label { font-size: 11px; font-weight: 500; color: #3a4860; text-transform: uppercase; letter-spacing: 0.08em; }
.field input {
  background: #070b12; border: 1px solid rgba(255,255,255,0.07);
  border-radius: 8px; color: #c8d3e0;
  padding: 9px 12px; font-family: 'DM Sans', sans-serif; font-size: 13px;
  outline: none; transition: border-color 0.2s; width: 100%; box-sizing: border-box;
}
.field input:focus { border-color: #f87171; }
.field input::placeholder { color: #2a3545; }

.msg { border-radius: 8px; padding: 9px 14px; font-size: 12px; font-weight: 500; margin-bottom: 14px; line-height: 1.5; }
.msg.error { background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); color: #f87171; }

.btn-admin {
  width: 100%;
  background: linear-gradient(135deg, #7f1d1d, #991b1b);
  border: 1px solid rgba(239,68,68,0.3); border-radius: 10px; color: #fca5a5;
  font-family: 'DM Sans', sans-serif; font-size: 13px; font-weight: 600;
  padding: 11px; cursor: pointer; transition: opacity 0.2s;
  display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 4px;
}
.btn-admin:hover:not(:disabled) { opacity: 0.85; }
.btn-admin:disabled { opacity: 0.4; cursor: not-allowed; }

.spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.7s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>