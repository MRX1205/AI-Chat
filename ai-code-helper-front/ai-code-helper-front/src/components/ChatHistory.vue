<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

const props = defineProps<{
  history: Array<{ id: number; title: string; active: boolean }>
}>()

const emit = defineEmits<{
  (e: 'switch-chat', id: number): void
  (e: 'new-chat'): void
}>()

const switchChat = (id: number) => {
  emit('switch-chat', id)
}
</script>

<template>
  <div class="history-list">
    <div 
      v-for="chat in history" 
      :key="chat.id" 
      @click="switchChat(chat.id)"
      :class="['history-item', { active: chat.active }]"
    >
      <div class="history-icon">💬</div>
      <div class="history-title">{{ chat.title }}</div>
    </div>
  </div>
</template>

<style scoped>
.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #202124;
  font-size: 14px;
}

.history-item:hover {
  background-color: #e8eaed;
}

.history-item.active {
  background-color: #e8f0fe;
  color: #1a73e8;
}

.history-icon {
  margin-right: 12px;
  font-size: 16px;
  color: #5f6368;
  flex-shrink: 0;
}

.history-item.active .history-icon {
  color: #1a73e8;
}

.history-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 