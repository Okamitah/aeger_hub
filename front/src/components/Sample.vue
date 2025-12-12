<template>
  <div class="container text-center">
    <h4 class="mx-2">Samples List</h4>
    <div class="row">
      <table class="table table-sm table-bordered table-hover">
        <thead>
          <tr>
            <th scope="col">Id</th>
            <th scope="col">Date</th>
            <th scope="col">String</th>
            <th scope="col">Float</th>
            <th scope="col">Type</th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody class="table-group-divider">
          <tr v-for="(sample, index) in samples" :key="index">
            <th scope="row">{{ sample.idSample }}</th>
            <td>
              <div class="input-group mb-3">
                <input
                  type="date"
                  class="form-control"
                  :value="sample.dateSample.substring(0, 10)"
                  @input="handleChangeDate(sample.idSample, $event.target.value)"
                />
                <button
                  class="btn btn-outline-primary"
                  type="button"
                  @click="updateDate(sample)"
                >
                  Save
                </button>
              </div>
            </td>
            <td>{{ sample.stringSample }}</td>
            <td>{{ sample.floatSample }}</td>
            <td>{{ sample.sampleType }}</td>
            <td>
              <button
                type="button"
                class="btn btn-outline-danger"
                @click="confirmRemoveSample(sample.idSample)"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="samples.length === 0" class="container text-center">
      No samples
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import '../styles/Sample.css'
import { GET_SAMPLES, LOCAL_HOST_SAMPLE, UPDATE_SAMPLES } from '../constants/back'

const samples = ref([])

const setSampleData = async () => {
  try {
    const response = await axios.get(GET_SAMPLES)
    samples.value = response.data
  } catch (error) {
    alert('Error occurred while loading data: ' + error.message)
  }
}

const confirmRemoveSample = (id) => {
  if (window.confirm('Are you sure?')) {
    removeSample(id)
  }
}

const removeSample = async (id) => {
  try {
    await axios.delete(LOCAL_HOST_SAMPLE + id)
    setSampleData()
  } catch (error) {
    alert('Error occurred in removeSample: ' + error.message)
  }
}

const handleChangeDate = (idSample, newDate) => {
  samples.value = samples.value.map((row) =>
    row.idSample === idSample ? { ...row, dateSample: newDate } : row
  )
}

const updateDate = async (sample) => {
  try {
    await axios.post(UPDATE_SAMPLES, sample)
    setSampleData()
  } catch (error) {
    alert('Error occurred in updateDate: ' + error.message)
  }
}

onMounted(() => {
  setSampleData()
})
</script>
