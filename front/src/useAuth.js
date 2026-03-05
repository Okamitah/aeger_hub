import { ref } from 'vue'

export const isLoggedIn = ref(false)
export const jwtToken = ref('')
export const username = ref('')
export const isAdmin = ref(false)

export function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken.value}`
    }
}

export function logout() {
    isLoggedIn.value = false
    jwtToken.value = ''
    username.value = ''
    isAdmin.value = false
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('username')
    localStorage.removeItem('is_admin')
}

export function restoreSession() {
    const storedToken = localStorage.getItem('jwt_token')
    const storedUsername = localStorage.getItem('username')
    const storedAdmin = localStorage.getItem('is_admin')
    if (storedToken && storedUsername) {
        jwtToken.value = storedToken
        username.value = storedUsername
        isAdmin.value = storedAdmin === 'true'
        isLoggedIn.value = true
        return true
    }
    return false
}