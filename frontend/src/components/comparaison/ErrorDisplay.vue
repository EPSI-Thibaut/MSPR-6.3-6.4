<script setup lang="ts">
defineProps({
  message: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div class="p-4 rounded-lg mb-6">
    {{ message }}
  </div>
</template>
