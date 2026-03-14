<template>
  <div class="auth-card">

    <!-- Tab switcher: Login / Register -->
    <div class="auth-tabs">
      <button class="auth-tab" :class="{ active: mode === 'login' }" @click="$emit('mode', 'login')">Sign In</button>
      <button class="auth-tab" :class="{ active: mode === 'register' }" @click="$emit('mode', 'register')">Register</button>
    </div>

    <!-- ────────────── LOGIN FORM ────────────── -->
    <div v-if="mode === 'login'" class="form-body">
      <div class="field">
        <label>Username</label>
        <input
          :value="loginUsername"
          @input="$emit('update:loginUsername', $event.target.value)"
          type="text"
          placeholder="your_username"
          autocomplete="username"
          @keyup.enter="$emit('login')"
        />
      </div>
      <div class="field">
        <label>Password</label>
        <input
          :value="loginPassword"
          @input="$emit('update:loginPassword', $event.target.value)"
          type="password"
          placeholder="••••••••"
          autocomplete="current-password"
          @keyup.enter="$emit('login')"
        />
      </div>

      <div v-if="loginError" class="msg error">{{ loginError }}</div>
      <div v-if="loginSuccess" class="msg success">{{ loginSuccess }}</div>

      <button class="btn-primary" :disabled="isLoading" @click="$emit('login')">
        {{ isLoading ? 'Signing in…' : 'Sign In' }}
      </button>

      <p class="switch-hint">
        No account?
        <button class="link-btn" @click="$emit('mode', 'register')">Create one</button>
      </p>
    </div>

    <!-- ────────────── REGISTER FORM ────────────── -->
    <div v-else class="form-body">

      <!-- Shared fields -->
      <div class="field">
        <label>Full Name <span class="req">*</span></label>
        <input v-model="reg.name" type="text" placeholder="Jane Doe" />
      </div>
      <div class="field-row">
        <div class="field">
          <label>Username <span class="req">*</span></label>
          <input v-model="reg.username" type="text" placeholder="jane_doe" autocomplete="username" />
        </div>
        <div class="field">
          <label>Password <span class="req">*</span></label>
          <input v-model="reg.password" type="password" placeholder="≥ 8 chars" autocomplete="new-password" />
        </div>
      </div>

      <!-- Patient-specific -->
      <template v-if="portal === 'patient'">
        <div class="field-row">
          <div class="field">
            <label>Date of Birth</label>
            <input v-model="reg.birthDate" type="date" />
          </div>
          <div class="field">
            <label>Sex</label>
            <select v-model="reg.sex">
              <option value="">— select —</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
        </div>
        <div class="field-row">
          <div class="field">
            <label>Height (cm)</label>
            <input v-model.number="reg.heightCm" type="number" placeholder="170" min="50" max="250" />
          </div>
          <div class="field">
            <label>Weight (kg)</label>
            <input v-model.number="reg.weightKg" type="number" placeholder="70" min="20" max="300" />
          </div>
        </div>
        <div class="field-row">
          <div class="field">
            <label>Illness</label>
            <select v-model="reg.illness">
              <option value="HEALTHY">Healthy</option>
              <option value="DIABETES">Diabetes</option>
              <option value="HYPERTENSION">Hypertension</option>
              <option value="OBESITY">Obesity</option>
            </select>
          </div>
          <div class="field">
            <label>Athleticism (0–5)</label>
            <select v-model="reg.athleticism">
              <option value="0">0 – Sedentary</option>
              <option value="1">1</option>
              <option value="2">2</option>
              <option value="3">3</option>
              <option value="4">4</option>
              <option value="5">5 – Athlete</option>
            </select>
          </div>
        </div>
        <div class="field-row checkboxes">
          <label class="check-label">
            <input type="checkbox" v-model="reg.smoker" /> Smoker
          </label>
          <label class="check-label">
            <input type="checkbox" v-model="reg.drinker" /> Drinker
          </label>
        </div>
      </template>

      <!-- Doctor-specific -->
      <template v-else>
        <div class="field">
          <label>Specialty</label>
          <input v-model="reg.specialty" type="text" placeholder="Cardiology" />
        </div>
        <div class="field">
          <label>Hospital / Clinic</label>
          <input v-model="reg.hospital" type="text" placeholder="General Hospital" />
        </div>
        <div class="field-row">
          <div class="field">
            <label>Sex</label>
            <select v-model="reg.sex">
              <option value="">— select —</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <div class="field">
            <label>Years of Experience</label>
            <input v-model.number="reg.yearsOfExperience" type="number" placeholder="5" min="0" />
          </div>
        </div>
      </template>

      <div v-if="regError" class="msg error">{{ regError }}</div>
      <div v-if="regSuccess" class="msg success">{{ regSuccess }}</div>

      <button class="btn-primary" :disabled="isLoading" @click="$emit('register')">
        {{ isLoading ? 'Creating account…' : portal === 'doctor' ? 'Submit Application' : 'Create Account' }}
      </button>

      <p class="switch-hint">
        Already registered?
        <button class="link-btn" @click="$emit('mode', 'login')">Sign in</button>
      </p>
    </div>

  </div>
</template>

<script setup>
defineProps({
  portal:        { type: String,  required: true },
  mode:          { type: String,  required: true },
  loginUsername: { type: String,  default: '' },
  loginPassword: { type: String,  default: '' },
  loginError:    { type: String,  default: '' },
  loginSuccess:  { type: String,  default: '' },
  reg:           { type: Object,  required: true },
  regError:      { type: String,  default: '' },
  regSuccess:    { type: String,  default: '' },
  isLoading:     { type: Boolean, default: false },
})

defineEmits([
  'update:loginUsername',
  'update:loginPassword',
  'switch',
  'mode',
  'login',
  'register',
  'to-register',
])
</script>

<style scoped>
.auth-card {
  background: rgba(13, 17, 27, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 16px;
  padding: 28px;
  width: 100%;
  max-width: 460px;
  backdrop-filter: blur(16px);
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.5);
}

/* ── Tabs ── */
.auth-tabs {
  display: flex;
  gap: 4px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 24px;
}
.auth-tab {
  flex: 1;
  background: none;
  border: none;
  color: #5a6478;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 7px;
  cursor: pointer;
  transition: all 0.2s;
}
.auth-tab.active {
  background: rgba(56, 189, 248, 0.12);
  color: #38bdf8;
}
.auth-tab:hover:not(.active) { color: #8899b0; }

/* ── Form body ── */
.form-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}
.field-row {
  display: flex;
  gap: 12px;
}
.field-row.checkboxes {
  align-items: center;
  gap: 20px;
  padding: 4px 0;
}

label {
  font-size: 11px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #3a4a60;
}
.req { color: #f87171; }

input[type="text"],
input[type="password"],
input[type="date"],
input[type="number"],
select {
  width: 100%;
  box-sizing: border-box;
  background: #0a0e18;
  border: 1px solid #1e2535;
  border-radius: 8px;
  color: #c8d3e0;
  padding: 9px 12px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  appearance: none;
}
input:focus,
select:focus {
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.08);
}
input::placeholder { color: #2a3548; }

select option { background: #0d1120; }

.check-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #4a5568;
  text-transform: none;
  letter-spacing: 0;
  cursor: pointer;
}
.check-label input[type="checkbox"] {
  width: 14px; height: 14px;
  accent-color: #38bdf8;
}

/* ── Messages ── */
.msg {
  padding: 9px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.5;
}
.msg.error   { background: #2d1010; color: #f87171; border: 1px solid rgba(239,68,68,0.25); }
.msg.success { background: #0d2318; color: #4ade80; border: 1px solid rgba(74,222,128,0.25); }

/* ── Primary button ── */
.btn-primary {
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 11px 16px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.15s, box-shadow 0.15s;
  box-shadow: 0 4px 14px rgba(56, 189, 248, 0.2);
  margin-top: 4px;
}
.btn-primary:hover:not(:disabled) {
  opacity: 0.9;
  box-shadow: 0 4px 20px rgba(56, 189, 248, 0.35);
}
.btn-primary:disabled { opacity: 0.35; cursor: not-allowed; }

/* ── Switch hint ── */
.switch-hint {
  font-size: 12px;
  color: #3a4555;
  text-align: center;
  margin-top: 2px;
}
.link-btn {
  background: none;
  border: none;
  color: #38bdf8;
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
  text-underline-offset: 2px;
}
.link-btn:hover { color: #7dd3fc; }
</style>