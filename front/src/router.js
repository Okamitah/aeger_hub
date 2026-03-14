import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn, userRole, restoreSession } from './useAuth.js'

import LandingView from './views/LandingView.vue'
import AdminView from './views/AdminView.vue'
import DoctorView from './views/DoctorView.vue'
import PatientView from './views/PatientView.vue'

const routes = [
    {
        path: '/',
        redirect: '/aegerhub'
    },
    {
        path: '/aegerhub',
        name: 'landing',
        component: LandingView,
        meta: { public: true }
    },
    {
        path: '/aegerhub/admin',
        name: 'admin',
        component: AdminView,
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/aegerhub/doctor',
        name: 'doctor',
        component: DoctorView,
        meta: { requiresAuth: true, role: 'doctor' }
    },
    {
        path: '/aegerhub/patient',
        name: 'patient',
        component: PatientView,
        meta: { requiresAuth: true, role: 'patient' }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/aegerhub'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, _from, next) => {
    restoreSession()

    if (to.meta.public) {
        if (isLoggedIn.value) {
            return next({ name: userRole.value || 'patient' })
        }
        return next()
    }

    if (to.meta.requiresAuth) {
        if (!isLoggedIn.value) {
            return next({ name: 'landing' })
        }
        if (to.meta.role && to.meta.role !== userRole.value) {
            return next({ name: userRole.value || 'landing' })
        }
        return next()
    }

    next()
})

export default router