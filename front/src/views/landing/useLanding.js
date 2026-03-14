import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { applyLogin } from '../../useAuth.js'

export function useLanding() {
    const router = useRouter()

    const portal = ref('patient')
    const mode = ref('login')

    const loginUsername = ref('')
    const loginPassword = ref('')
    const loginError = ref('')
    const loginSuccess = ref('')

    const regError = ref('')
    const regSuccess = ref('')
    const isLoading = ref(false)

    const reg = reactive({
        username: '', password: '', name: '',
        birthDate: '', sex: '', heightCm: null, weightKg: null,
        illness: 'HEALTHY', athleticism: '0', smoker: false, drinker: false,
        specialty: '', hospital: '', yearsOfExperience: null,
    })

    const showAdminModal = ref(false)
    const adminUsername = ref('')
    const adminPassword = ref('')
    const adminError = ref('')

    const authCardRef = ref(null)

    let secretClicks = 0
    let secretTimer = null

    function switchPortal(p) {
        portal.value = p
        clearMessages()
    }

    function clearMessages() {
        loginError.value = ''
        loginSuccess.value = ''
        regError.value = ''
        regSuccess.value = ''
        adminError.value = ''
    }

    function scrollToRegister() {
        mode.value = 'register'
        clearMessages()
        nextTick(() => authCardRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' }))
    }

    function handleSecretClick() {
        secretClicks++
        clearTimeout(secretTimer)
        secretTimer = setTimeout(() => { secretClicks = 0 }, 700)
        if (secretClicks >= 3) {
            secretClicks = 0
            showAdminModal.value = true
            adminUsername.value = ''
            adminPassword.value = ''
            adminError.value = ''
        }
    }

    async function handleLogin() {
        if (!loginUsername.value.trim() || !loginPassword.value) {
            loginError.value = 'Please fill in all fields.'
            return
        }
        isLoading.value = true
        loginError.value = ''
        try {
            const res = await fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: loginUsername.value.trim(), password: loginPassword.value })
            })
            const data = await res.json()
            if (data.success && data.token) {
                const serverRole = data.role || 'patient'
                if (serverRole !== portal.value) {
                    loginError.value = `This account is registered as a ${serverRole}. Please switch to the correct portal.`
                    return
                }
                router.push({ name: applyLogin(data.token, data.username, serverRole) })
            } else {
                loginError.value = data.message || 'Invalid username or password.'
            }
        } catch {
            loginError.value = 'Network error — please check your connection.'
        } finally {
            isLoading.value = false
        }
    }

    async function handleAdminLogin() {
        if (!adminUsername.value || !adminPassword.value) {
            adminError.value = 'Please fill in all fields.'
            return
        }
        isLoading.value = true
        adminError.value = ''
        try {
            const res = await fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: adminUsername.value.trim(), password: adminPassword.value })
            })
            const data = await res.json()
            if (data.success && data.token && data.username === 'admin') {
                applyLogin(data.token, data.username, 'admin')
                showAdminModal.value = false
                router.push({ name: 'admin' })
            } else if (data.success && data.username !== 'admin') {
                adminError.value = 'This account does not have admin privileges.'
            } else {
                adminError.value = data.message || 'Invalid credentials.'
            }
        } catch {
            adminError.value = 'Network error — please try again.'
        } finally {
            isLoading.value = false
        }
    }

    async function handleRegister() {
        if (!reg.username || !reg.password || !reg.name) {
            regError.value = 'Username, password and full name are required.'
            return
        }
        if (reg.password.length < 8) {
            regError.value = 'Password must be at least 8 characters.'
            return
        }
        isLoading.value = true
        regError.value = ''
        regSuccess.value = ''
        try {
            const endpoint = portal.value === 'patient' ? '/patients/register' : '/doctors/register'
            const payload = portal.value === 'patient'
                ? {
                    username: reg.username, password: reg.password, name: reg.name,
                    birthDate: reg.birthDate || null, sex: reg.sex || null,
                    heightCm: reg.heightCm || null, weightKg: reg.weightKg || null,
                    illness: reg.illness, athleticism: parseInt(reg.athleticism),
                    smoker: reg.smoker, drinker: reg.drinker,
                }
                : {
                    username: reg.username, password: reg.password, name: reg.name,
                    specialty: reg.specialty, hospital: reg.hospital,
                    yearsOfExperience: reg.yearsOfExperience || 0,
                    sex: reg.sex || null, verified: false,
                }
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            if (res.ok) {
                if (portal.value === 'patient') {
                    regSuccess.value = '✓ Account created! Redirecting to sign in…'
                    Object.assign(reg, {
                        username: '', password: '', name: '', birthDate: '', sex: '',
                        heightCm: null, weightKg: null, illness: 'HEALTHY', athleticism: '0',
                        smoker: false, drinker: false
                    })
                    setTimeout(() => { mode.value = 'login'; regSuccess.value = '' }, 1800)
                } else {
                    regSuccess.value = '✓ Application submitted! An admin will review and activate your account.'
                    Object.assign(reg, {
                        username: '', password: '', name: '', specialty: '',
                        hospital: '', yearsOfExperience: null, sex: ''
                    })
                }
            } else {
                const data = await res.json().catch(() => ({}))
                regError.value = data.message || `Registration failed (${res.status}).`
            }
        } catch {
            regError.value = 'Network error — please check your connection.'
        } finally {
            isLoading.value = false
        }
    }

    return {
        portal, mode,
        loginUsername, loginPassword, loginError, loginSuccess,
        reg, regError, regSuccess,
        isLoading,
        showAdminModal, adminUsername, adminPassword, adminError,
        authCardRef,
        switchPortal, clearMessages, scrollToRegister, handleSecretClick,
        handleLogin, handleAdminLogin, handleRegister,
    }
}