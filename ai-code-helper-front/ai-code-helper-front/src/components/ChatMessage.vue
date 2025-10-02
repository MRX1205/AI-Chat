<script setup lang="ts">
import { defineProps } from 'vue'

defineProps<{
  type: 'user' | 'ai'
  content: string
  aiAvatar?: string
}>()
</script>

<template>
  <div :class="['message', type === 'user' ? 'user-message' : 'ai-message']">
    <div class="avatar">
      <template v-if="type === 'user'">👤</template>
      <img v-else-if="aiAvatar" :src="aiAvatar" alt="AI头像" class="ai-custom-avatar" />
      <template v-else>🤖</template>
    </div>
    <div class="message-content">
      <div class="message-text" v-html="content.replace(/\n/g, '<br>')"></div>
    </div>
  </div>
</template>

<style scoped>
.message {
  display: flex;
  margin-bottom: 8px;
  max-width: 90%;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #e8f0fe;
  margin: 0 12px;
  font-size: 16px;
  flex-shrink: 0;
  overflow: hidden;
}

.ai-custom-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-message .avatar {
  background-color: #d2e3fc;
}

.message-content {
  background-color: #f1f3f4;
  padding: 16px 20px;
  border-radius: 18px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
}

.user-message .message-content {
  background-color: #e8f0fe;
}

.message-text {
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 15px;
}
</style> 