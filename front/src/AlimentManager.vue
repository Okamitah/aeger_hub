<template>
  <div>
    <h3>Aliment / Nutrition Database</h3>

    <div>
      <button @click="tab = 'search'" :disabled="tab === 'search'">Search by Name</button>
      <button @click="tab = 'category'" :disabled="tab === 'category'">Bulk by Category</button>
      <button @click="tab = 'saved'" :disabled="tab === 'saved'">Saved ({{ saved.length }})</button>
    </div>

    <div v-if="tab === 'search'">
      <h4>Search Open Food Facts</h4>
      <div>
        <input v-model="searchQuery" type="text" placeholder="e.g. apple, chicken breast, oats" />
        <input v-model.number="searchPageSize" type="number" min="1" max="50" style="width:60px" />
        <button @click="doSearch" :disabled="isSearching || !searchQuery.trim()">
          {{ isSearching ? 'Searching...' : 'Search' }}
        </button>
      </div>

      <div v-if="searchMessage"><strong>{{ searchMessage }}</strong></div>

      <div v-if="searchResults.length > 0">
        <p>{{ searchResults.length }} results — click a row to import it.</p>
        <table border="1">
          <thead>
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>kcal</th>
              <th>Protein (g)</th>
              <th>Carbs (g)</th>
              <th>Fat (g)</th>
              <th>Fiber (g)</th>
              <th>Sugar (g)</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in searchResults" :key="a.offId">
              <td>{{ a.name }}</td>
              <td>{{ a.category || '—' }}</td>
              <td>{{ fmt(a.calories) }}</td>
              <td>{{ fmt(a.proteinG) }}</td>
              <td>{{ fmt(a.carbohydratesG) }}</td>
              <td>{{ fmt(a.fatG) }}</td>
              <td>{{ fmt(a.fiberG) }}</td>
              <td>{{ fmt(a.sugarG) }}</td>
              <td>
                <button @click="importOne(a)" :disabled="importing[a.offId]">
                  {{ importing[a.offId] ? 'Saving...' : (importedIds.has(a.offId) ? '✓ Saved' : 'Import') }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="tab === 'category'">
      <h4>Bulk Import by Category</h4>
      <div>
        <select v-model="selectedCategory">
          <option v-for="c in categories" :key="c.tag" :value="c.tag">{{ c.label }}</option>
        </select>
        <input v-model.number="categoryPageSize" type="number" min="1" max="100" style="width:60px" />
        <button @click="doPreviewCategory" :disabled="isCategoryLoading">
          {{ isCategoryLoading ? 'Loading...' : 'Preview' }}
        </button>
        <button @click="doBulkImport" :disabled="isBulkImporting || categoryResults.length === 0">
          {{ isBulkImporting ? 'Importing...' : `Import All (${categoryResults.length})` }}
        </button>
      </div>

      <div v-if="categoryMessage"><strong>{{ categoryMessage }}</strong></div>

      <div v-if="categoryResults.length > 0">
        <table border="1">
          <thead>
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>kcal</th>
              <th>Protein (g)</th>
              <th>Carbs (g)</th>
              <th>Fat (g)</th>
              <th>Fiber (g)</th>
              <th>Sugar (g)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in categoryResults" :key="a.offId">
              <td>{{ a.name }}</td>
              <td>{{ a.category || '—' }}</td>
              <td>{{ fmt(a.calories) }}</td>
              <td>{{ fmt(a.proteinG) }}</td>
              <td>{{ fmt(a.carbohydratesG) }}</td>
              <td>{{ fmt(a.fatG) }}</td>
              <td>{{ fmt(a.fiberG) }}</td>
              <td>{{ fmt(a.sugarG) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="tab === 'saved'">
      <h4>Saved Aliments</h4>
      <button @click="loadSaved" :disabled="isLoadingSaved">
        {{ isLoadingSaved ? 'Loading...' : 'Refresh' }}
      </button>

      <div v-if="saved.length > 0">
        <table border="1">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Category</th>
              <th>kcal</th>
              <th>Protein (g)</th>
              <th>Carbs (g)</th>
              <th>Fat (g)</th>
              <th>Fiber (g)</th>
              <th>Sugar (g)</th>
              <th>Sodium (mg)</th>
              <th>Vit C (mg)</th>
              <th>Vit D (µg)</th>
              <th>Ca (mg)</th>
              <th>Fe (mg)</th>
              <th>Mg (mg)</th>
              <th>K (mg)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in saved" :key="a.id">
              <td>{{ a.id }}</td>
              <td>{{ a.name }}</td>
              <td>{{ a.category || '—' }}</td>
              <td>{{ fmt(a.calories) }}</td>
              <td>{{ fmt(a.proteinG) }}</td>
              <td>{{ fmt(a.carbohydratesG) }}</td>
              <td>{{ fmt(a.fatG) }}</td>
              <td>{{ fmt(a.fiberG) }}</td>
              <td>{{ fmt(a.sugarG) }}</td>
              <td>{{ fmt(a.sodiumMg) }}</td>
              <td>{{ fmt(a.vitaminCMg) }}</td>
              <td>{{ fmt(a.vitaminDUg) }}</td>
              <td>{{ fmt(a.calciumMg) }}</td>
              <td>{{ fmt(a.ironMg) }}</td>
              <td>{{ fmt(a.magnesiumMg) }}</td>
              <td>{{ fmt(a.potassiumMg) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else-if="!isLoadingSaved">No aliments saved yet.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  token: { type: String, required: true }
})

const tab = ref('search')

const searchQuery = ref('')
const searchPageSize = ref(10)
const isSearching = ref(false)
const searchResults = ref([])
const searchMessage = ref('')
const importing = ref({})
const importedIds = ref(new Set())

const selectedCategory = ref('en:fruits')
const categoryPageSize = ref(20)
const isCategoryLoading = ref(false)
const isBulkImporting = ref(false)
const categoryResults = ref([])
const categoryMessage = ref('')

const categories = [
  { tag: 'en:fruits', label: 'Fruits' },
  { tag: 'en:vegetables', label: 'Vegetables' },
  { tag: 'en:dairy', label: 'Dairy' },
  { tag: 'en:meats', label: 'Meats' },
  { tag: 'en:fish', label: 'Fish & Seafood' },
  { tag: 'en:cereals', label: 'Cereals & Grains' },
  { tag: 'en:legumes', label: 'Legumes' },
  { tag: 'en:nuts', label: 'Nuts & Seeds' },
  { tag: 'en:beverages', label: 'Beverages' },
  { tag: 'en:snacks', label: 'Snacks' },
]

const saved = ref([])
const isLoadingSaved = ref(false)

onMounted(() => loadSaved())

function headers() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${props.token}`
  }
}

function fmt(v) {
  return v == null ? '—' : Number(v).toFixed(1)
}

async function doSearch() {
  isSearching.value = true
  searchMessage.value = ''
  searchResults.value = []
  try {
    const res = await fetch(`/aliments/search?q=${encodeURIComponent(searchQuery.value)}&pageSize=${searchPageSize.value}`, {
      headers: headers()
    })
    if (!res.ok) throw new Error(`Status ${res.status}`)
    searchResults.value = await res.json()
    if (searchResults.value.length === 0) searchMessage.value = 'No results found.'
  } catch (e) {
    searchMessage.value = 'Error: ' + e.message
  } finally {
    isSearching.value = false
  }
}

async function importOne(aliment) {
  importing.value[aliment.offId] = true
  try {
    const res = await fetch('/aliments/import', {
      method: 'POST',
      headers: headers(),
      body: JSON.stringify(aliment)
    })
    if (!res.ok) throw new Error(`Status ${res.status}`)
    importedIds.value.add(aliment.offId)
    saved.value = []
  } catch (e) {
    alert('Import failed: ' + e.message)
  } finally {
    importing.value[aliment.offId] = false
  }
}

async function doPreviewCategory() {
  isCategoryLoading.value = true
  categoryMessage.value = ''
  categoryResults.value = []
  try {
    const res = await fetch(`/aliments/category?tag=${selectedCategory.value}&pageSize=${categoryPageSize.value}`, {
      headers: headers()
    })
    if (!res.ok) throw new Error(`Status ${res.status}`)
    categoryResults.value = await res.json()
    if (categoryResults.value.length === 0) categoryMessage.value = 'No results found.'
  } catch (e) {
    categoryMessage.value = 'Error: ' + e.message
  } finally {
    isCategoryLoading.value = false
  }
}

async function doBulkImport() {
  isBulkImporting.value = true
  categoryMessage.value = ''
  try {
    const res = await fetch('/aliments/import/bulk', {
      method: 'POST',
      headers: headers(),
      body: JSON.stringify(categoryResults.value)
    })
    if (!res.ok) throw new Error(`Status ${res.status}`)
    const imported = await res.json()
    categoryMessage.value = `Successfully imported ${imported.length} aliments!`
    saved.value = []
  } catch (e) {
    categoryMessage.value = 'Error: ' + e.message
  } finally {
    isBulkImporting.value = false
  }
}

async function loadSaved() {
  isLoadingSaved.value = true
  try {
    const res = await fetch('/aliments', { headers: headers() })
    if (!res.ok) throw new Error(`Status ${res.status}`)
    saved.value = await res.json()
  } catch (e) {
    console.error('Failed to load aliments', e)
  } finally {
    isLoadingSaved.value = false
  }
}
</script>
