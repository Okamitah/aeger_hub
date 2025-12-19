<script setup>
import HelloWorld from './components/HelloWorld.vue'
import { onMounted } from 'vue'

onMounted(async () => {
  try {
    const backendUrl = 'http://172.31.249.107:8080/api/'
    console.log('Fetching from backend:', backendUrl)
    const res = await fetch(backendUrl)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const data = await res.json()
    console.log('Backend response:', data)
  } catch (err) {
    console.error('Failed to reach backend:', err)
  }
})
</script>

<template>
  <header>
    <div class="wrapper">
      <HelloWorld msg="Hello World!" />
    </div>
  </header>
</template>

<style scoped>
header {
  line-height: 1.5;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
