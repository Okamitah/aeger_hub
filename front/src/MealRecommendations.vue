<template>
  <div>
    <h3>Meal Recommendations</h3>

    <div v-if="loading">Loading recommendations...</div>

    <div v-else-if="error">
      <p style="color: red;">{{ error }}</p>
    </div>

    <div v-else>
      <p>Based on condition: <strong>{{ illness }}</strong></p>

      <template v-if="meals">
        <div v-for="mealType in ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK']" :key="mealType">
          <div v-if="meals[mealType] && meals[mealType].length > 0">
            <h4>{{ capitalize(mealType) }} Options</h4>
            <table border="1" cellpadding="6" cellspacing="0">
              <thead>
                <tr>
                  <th>Meal</th>
                  <th>Description</th>
                  <th>Calories</th>
                  <th>Protein (g)</th>
                  <th>Carbs (g)</th>
                  <th>Fats (g)</th>
                  <th>Fiber (g)</th>
                  <th>Sugar (g)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="meal in meals[mealType]" :key="meal.id">
                  <td>{{ meal.name }}</td>
                  <td>{{ meal.description }}</td>
                  <td>{{ meal.calories }}</td>
                  <td>{{ meal.protein }}</td>
                  <td>{{ meal.carbs }}</td>
                  <td>{{ meal.fats }}</td>
                  <td>{{ meal.fiber }}</td>
                  <td>{{ meal.sugar }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>

      <template v-if="patientId">
        <hr style="margin: 24px 0;" />
        <h3>Recommended Foods from Database</h3>

        <div v-if="alimentLoading">Loading food recommendations...</div>

        <div v-else-if="alimentError" style="color: red;">{{ alimentError }}</div>

        <div v-else-if="alimentGroups && Object.keys(alimentGroups).length > 0">
          <p style="color: #555;">
            Foods below meet the nutritional thresholds safe for
            <strong>{{ illness }}</strong> patients (per 100g).
          </p>

          <div v-for="(aliments, category) in alimentGroups" :key="category" style="margin-bottom: 24px;">
            <h4 style="text-transform: capitalize;">{{ formatCategory(category) }}</h4>
            <table border="1" cellpadding="6" cellspacing="0">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Calories</th>
                  <th>Protein (g)</th>
                  <th>Carbs (g)</th>
                  <th>Fat (g)</th>
                  <th>Fiber (g)</th>
                  <th>Sugar (g)</th>
                  <th>Sodium (mg)</th>
                  <th>Cholesterol (mg)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="aliment in aliments" :key="aliment.id">
                  <td>{{ aliment.name }}</td>
                  <td>{{ fmt(aliment.calories) }}</td>
                  <td>{{ fmt(aliment.proteinG) }}</td>
                  <td>{{ fmt(aliment.carbohydratesG) }}</td>
                  <td>{{ fmt(aliment.fatG) }}</td>
                  <td>{{ fmt(aliment.fiberG) }}</td>
                  <td>{{ fmt(aliment.sugarG) }}</td>
                  <td>{{ fmt(aliment.sodiumMg) }}</td>
                  <td>{{ fmt(aliment.cholesterolMg) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else>
          <p style="color: #888;">
            No specific food recommendations found in the database for this condition.
            Consider importing foods via the Aliment Manager.
          </p>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  illness: {
    type: String,
    required: true
  },
  token: {
    type: String,
    required: true
  },
  patientId: {
    type: [Number, String],
    default: null
  }
})

const meals         = ref(null)
const loading       = ref(true)
const error         = ref('')

const alimentGroups  = ref(null)
const alimentLoading = ref(false)
const alimentError   = ref('')

onMounted(async () => {
  await Promise.all([
    loadMealRecommendations(),
    props.patientId ? loadAlimentRecommendations() : Promise.resolve()
  ])
})

async function loadMealRecommendations() {
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(`/meals/recommendations/${props.illness}`, {
      headers: { 'Authorization': `Bearer ${props.token}` }
    })
    if (res.ok) {
      meals.value = await res.json()
    } else if (res.status === 401 || res.status === 403) {
      error.value = 'Authentication failed. Please log in again.'
    } else {
      error.value = 'Failed to load meal recommendations.'
    }
  } catch (err) {
    console.error('Error loading meals:', err)
    error.value = 'Error loading meal recommendations.'
  } finally {
    loading.value = false
  }
}

async function loadAlimentRecommendations() {
  alimentLoading.value = true
  alimentError.value = ''
  try {
    const res = await fetch(`/patients/${props.patientId}/recommendations`, {
      headers: { 'Authorization': `Bearer ${props.token}` }
    })
    if (res.ok) {
      alimentGroups.value = await res.json()
    } else if (res.status === 404) {
      alimentError.value = 'Patient not found.'
    } else if (res.status === 401 || res.status === 403) {
      alimentError.value = 'Authentication failed.'
    } else {
      alimentError.value = 'Failed to load food recommendations.'
    }
  } catch (err) {
    console.error('Error loading aliment recommendations:', err)
    alimentError.value = 'Error loading food recommendations.'
  } finally {
    alimentLoading.value = false
  }
}

function capitalize(str) {
  if (!str) return ''
  return str.charAt(0) + str.slice(1).toLowerCase()
}

function formatCategory(cat) {
  return cat.replace(/-/g, ' ')
}

function fmt(val) {
  if (val === null || val === undefined) return '—'
  return Math.round(val * 10) / 10
}
</script>
