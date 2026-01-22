<template>
  <div>
    <h3>Meal Recommendations</h3>
    
    <div v-if="loading">Loading meal recommendations...</div>
    
    <div v-else-if="error">
      <p>{{ error }}</p>
    </div>
    
    <div v-else-if="meals">
      <p>Based on your condition: <strong>{{ illness }}</strong></p>
      
      <div v-if="meals.BREAKFAST && meals.BREAKFAST.length > 0">
        <h4>Breakfast Options</h4>
        <table border="1">
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
            <tr v-for="meal in meals.BREAKFAST" :key="meal.id">
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
      
      <div v-if="meals.LUNCH && meals.LUNCH.length > 0">
        <h4>Lunch Options</h4>
        <table border="1">
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
            <tr v-for="meal in meals.LUNCH" :key="meal.id">
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
      
      <div v-if="meals.DINNER && meals.DINNER.length > 0">
        <h4>Dinner Options</h4>
        <table border="1">
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
            <tr v-for="meal in meals.DINNER" :key="meal.id">
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
      
      <div v-if="meals.SNACK && meals.SNACK.length > 0">
        <h4>Snack Options</h4>
        <table border="1">
          <thead>
            <tr>
              <th>Snack</th>
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
            <tr v-for="meal in meals.SNACK" :key="meal.id">
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
  }
})

const meals = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  await loadMealRecommendations()
})

async function loadMealRecommendations() {
  loading.value = true
  error.value = ''
  
  try {
    const res = await fetch(`/meals/recommendations/${props.illness}`, {
      headers: {
        'Authorization': `Bearer ${props.token}`
      }
    })
    
    if (res.ok) {
      meals.value = await res.json()
      console.log('Loaded meals:', meals.value)
    } else if (res.status === 401 || res.status === 403) {
      error.value = 'Authentication failed. Please log in again.'
    } else {
      error.value = 'Failed to load meal recommendations'
    }
  } catch (err) {
    console.error('Error loading meals:', err)
    error.value = 'Error loading meal recommendations'
  } finally {
    loading.value = false
  }
}
</script>
