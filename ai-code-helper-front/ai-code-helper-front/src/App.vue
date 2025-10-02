<script setup lang="ts">
import { ref, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
import apiService from './services/api'
import ChatHistory from './components/ChatHistory.vue'
import UserProfile from './components/UserProfile.vue'
import ChatMessage from './components/ChatMessage.vue'

// UserProfile组件引用
const userProfile = ref<InstanceType<typeof UserProfile> | null>(null)

// 生成随机会话ID
const sessionId = ref(Math.floor(Math.random() * 1000000))

// 消息列表
const messages = ref<Array<{ type: 'user' | 'ai'; content: string }>>([])

// AI头像
const aiAvatar = ref('')

// 可用的AI头像列表
const availableAvatars = [
  '/avatars/ai-avatar-1.png',
  '/avatars/ai-avatar-2.png',
  '/avatars/ai-avatar-3.png',
  '/avatars/default-ai.png'
]

// 当前选择的AI头像
const selectedAvatarIndex = ref(3) // 默认使用default-ai.png

// 更新AI头像
const updateAiAvatar = (index: number) => {
  selectedAvatarIndex.value = index
  aiAvatar.value = availableAvatars[index]
  localStorage.setItem('aiAvatarIndex', index.toString())
}

// 历史对话列表
const chatHistory = ref<Array<{ id: number; title: string; active: boolean }>>([
  { id: sessionId.value, title: '新对话', active: true },
  { id: 100001, title: '编程基础学习', active: false },
  { id: 100002, title: 'JavaScript深入讲解', active: false },
  { id: 100003, title: '面试技巧问答', active: false },
])

// 登录状态
const isLoggedIn = ref(false)

// 输入内容
const inputMessage = ref('')

// 是否正在加载
const loading = ref(false)

// 当前的EventSource连接
let currentEventSource: EventSource | null = null

// 侧边栏状态
const sidebarCollapsed = ref(false)

// 设置面板状态
const showSettings = ref(false)

// 深色模式
const darkMode = ref(false)

// 自动滚动到底部
const chatContainer = ref<HTMLElement | null>(null)
const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 切换设置面板
const toggleSettings = () => {
  showSettings.value = !showSettings.value
}

// 切换主题
const toggleTheme = () => {
  darkMode.value = !darkMode.value
  if (darkMode.value) {
    document.body.classList.add('dark-theme')
  } else {
    document.body.classList.remove('dark-theme')
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({ type: 'user', content: userMessage })
  
  // 清空输入框
  inputMessage.value = ''
  
  // 滚动到底部
  await scrollToBottom()
  
  // 设置加载状态
  loading.value = true
  
  try {
    // 添加一个空的AI消息占位
    const aiMessageIndex = messages.value.length
    messages.value.push({ type: 'ai', content: '' })
    
    // 关闭之前的连接
    if (currentEventSource) {
      apiService.closeEventSource(currentEventSource)
    }
    
    // 创建新的EventSource连接
    currentEventSource = apiService.chatStream(sessionId.value, userMessage)
    
    // 监听消息
    currentEventSource.onmessage = (event) => {
      // 更新AI回复内容
      if (messages.value[aiMessageIndex]) {
        messages.value[aiMessageIndex].content += event.data
        scrollToBottom()
      }
    }
    
    // 处理连接关闭
    currentEventSource.onerror = () => {
      if (currentEventSource) {
        apiService.closeEventSource(currentEventSource)
        currentEventSource = null
      }
      loading.value = false
    }
    
    // 处理连接结束
    currentEventSource.addEventListener('end', () => {
      if (currentEventSource) {
        apiService.closeEventSource(currentEventSource)
        currentEventSource = null
      }
      loading.value = false
    })
  } catch (error) {
    console.error('Error:', error)
    loading.value = false
  }
}

// 停止AI回复
const stopAIResponse = () => {
  if (currentEventSource) {
    apiService.closeEventSource(currentEventSource)
    currentEventSource = null
    loading.value = false
  }
}

// 处理按键事件
const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 切换对话
const switchChat = (id: number) => {
  chatHistory.value.forEach(chat => {
    chat.active = chat.id === id
  })
  // 实际应用中这里需要加载对应ID的聊天记录
  sessionId.value = id
  
  // 模拟切换聊天的效果，实际中需要从后端获取对应的聊天记录
  messages.value = []
  messages.value.push({ 
    type: 'ai', 
    content: `已切换到会话 ID: ${id}。你好！我是AI编程小助手，很高兴为你解答编程学习和求职面试相关的问题。` 
  })
}

// 创建新对话
const createNewChat = () => {
  const newId = Math.floor(Math.random() * 1000000)
  chatHistory.value.forEach(chat => {
    chat.active = false
  })
  chatHistory.value.unshift({ id: newId, title: '新对话', active: true })
  sessionId.value = newId
  messages.value = []
  messages.value.push({ 
    type: 'ai', 
    content: '你好！我是AI编程小助手，很高兴为你解答编程学习和求职面试相关的问题。请告诉我你需要什么帮助？' 
  })
}

// 登录处理
const handleLogin = () => {
  isLoggedIn.value = true
}

// 退出登录
const handleLogout = () => {
  isLoggedIn.value = false
}

// 组件卸载前关闭SSE连接
onBeforeUnmount(() => {
  if (currentEventSource) {
    apiService.closeEventSource(currentEventSource)
  }
})

// 确保初始化时加载默认头像
onMounted(() => {
  // 初始化默认头像
  aiAvatar.value = availableAvatars[selectedAvatarIndex.value]
  
  // 添加欢迎消息
  messages.value.push({ 
    type: 'ai', 
    content: '你好！我是AI编程小助手，很高兴为你解答编程学习和求职面试相关的问题。请告诉我你需要什么帮助？' 
  })
  
  // 初始滚动到底部
  scrollToBottom()
  
  // 检查本地存储中的主题设置
  const savedDarkMode = localStorage.getItem('darkMode') === 'true'
  if (savedDarkMode) {
    darkMode.value = true
    document.body.classList.add('dark-theme')
  }
  
  // 初始化AI头像
  const savedAvatarIndex = localStorage.getItem('aiAvatarIndex')
  if (savedAvatarIndex && !isNaN(Number(savedAvatarIndex))) {
    const index = Number(savedAvatarIndex)
    if (index >= 0 && index < availableAvatars.length) {
      selectedAvatarIndex.value = index
      aiAvatar.value = availableAvatars[index]
    } else {
      aiAvatar.value = availableAvatars[selectedAvatarIndex.value]
    }
  } else {
    aiAvatar.value = availableAvatars[selectedAvatarIndex.value]
  }
})

// 监听主题变化并保存到本地存储
watch(darkMode, (newValue) => {
  localStorage.setItem('darkMode', newValue.toString())
})
</script>

<template>
  <div :class="['app-container', {'dark-theme': darkMode}]">
    <!-- 左侧边栏 - 历史对话列表 -->
    <div :class="['sidebar', {'collapsed': sidebarCollapsed}]">
      <div class="sidebar-top">
        <button class="menu-button" @click="toggleSidebar">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>
        </button>
        <!-- 用户信息 -->
        <div v-if="isLoggedIn && !sidebarCollapsed" class="sidebar-user-info">
          <img :src="userProfile?.userAvatar" alt="用户头像" class="sidebar-user-avatar" />
          <span class="sidebar-username">{{ userProfile?.nickname }}</span>
        </div>
      </div>
      
      <div v-if="!sidebarCollapsed">
        <button @click="createNewChat" class="new-chat-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
          发起新对话
        </button>
        
        <div class="section-header">Gem</div>
        
        <div class="sidebar-options">
          <div class="sidebar-option">
            <span class="option-icon">💻</span>
            <span class="option-text">编码助手</span>
          </div>
          <div class="sidebar-option">
            <span class="option-icon">🔍</span>
            <span class="option-text">探索 Gem</span>
          </div>
        </div>
        
        <div class="section-header">近期对话</div>
        <ChatHistory 
          :history="chatHistory"
          @switch-chat="switchChat"
          @new-chat="createNewChat"
        />
      </div>
      
      <div class="sidebar-footer">
        <button @click="toggleSettings" class="settings-button">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
          <span v-if="!sidebarCollapsed" class="settings-text">设置</span>
        </button>
      </div>
    </div>
    
    <!-- 设置面板 -->
    <div v-if="showSettings" class="settings-panel">
      <div class="settings-header">
        <h2>设置</h2>
        <button class="close-settings" @click="toggleSettings">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
      </div>
      
      <div class="settings-content">
        <div class="settings-section">
          <h3>主题设置</h3>
          <div class="theme-toggle">
            <span>深色模式</span>
            <label class="switch">
              <input type="checkbox" v-model="darkMode" @change="toggleTheme">
              <span class="slider"></span>
            </label>
          </div>
        </div>
        
        <div class="settings-section">
          <h3>AI头像设置</h3>
          <div class="ai-avatar-selection">
            <div class="avatar-options">
              <div 
                v-for="(avatar, index) in availableAvatars" 
                :key="index"
                :class="['avatar-option', { 'selected': selectedAvatarIndex === index }]"
                @click="updateAiAvatar(index)"
              >
                <img :src="avatar" alt="AI头像选项" />
              </div>
            </div>
            <div class="selected-avatar-preview">
              <p>当前选择的头像:</p>
              <img :src="aiAvatar || availableAvatars[selectedAvatarIndex]" alt="当前AI头像" />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 右侧主内容区 -->
    <main class="main-content">
      <!-- 头部导航 -->
      <header class="main-header">
        <div class="header-left">
          <h1 class="gemini-title">掠影航猫</h1>
          <div class="model-selector">
            <span class="model-name">2.5 Pro</span>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>
          </div>
        </div>
        <div class="header-right">
          <UserProfile 
            ref="userProfile"
            :is-logged-in="isLoggedIn"
            @login="handleLogin"
            @logout="handleLogout"
          />
        </div>
      </header>
      
      <!-- 聊天内容区 -->
      <div class="chat-container" ref="chatContainer">
        <ChatMessage 
          v-for="(message, index) in messages" 
          :key="index" 
          :type="message.type" 
          :content="message.content"
          :ai-avatar="message.type === 'ai' && aiAvatar ? aiAvatar : undefined"
        />
        <div v-if="loading" class="loading-indicator">
          <div class="loading-dot"></div>
          <div class="loading-dot"></div>
          <div class="loading-dot"></div>
        </div>
      </div>
      
      <!-- 输入区域 -->
      <footer class="input-container">
        <div class="input-wrapper">
          <textarea 
            v-model="inputMessage"
            @keydown="handleKeyDown"
            placeholder="问一问掠影航猫"
            :disabled="loading"
            class="message-input"
          ></textarea>
          
          <div class="input-actions no-border">
            <button v-if="loading" class="stop-button" @click="stopAIResponse">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="6" width="12" height="12"></rect></svg>
              停止回复
            </button>
            <button class="send-button" @click="sendMessage" :disabled="loading || !inputMessage.trim()">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
            </button>
          </div>
        </div>
      </footer>
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Google Sans', Arial, sans-serif;
  background-color: #f8f9fa;
  color: #202124;
  line-height: 1.5;
  transition: background-color 0.3s, color 0.3s;
}

body.dark-theme {
  background-color: #202124;
  color: #e8eaed;
}

/* 整体布局 */
.app-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.dark-theme {
  --bg-primary: #202124;
  --bg-secondary: #303134;
  --border-color: #5f6368;
  --text-primary: #e8eaed;
  --text-secondary: #9aa0a6;
  --accent-color: #8ab4f8;
  --hover-color: #3c4043;
}

/* 左侧边栏样式 */
.sidebar {
  width: 280px;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
  border-right: 1px solid #dadce0;
  padding: 8px;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.dark-theme .sidebar {
  background-color: var(--bg-secondary);
  border-right-color: var(--border-color);
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-top {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 12px 8px;
  margin-bottom: 8px;
}

.menu-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  color: #5f6368;
}

.dark-theme .menu-button {
  color: var(--text-secondary);
}

.menu-button:hover {
  background-color: #e8eaed;
}

.dark-theme .menu-button:hover {
  background-color: var(--hover-color);
}

.sidebar-user-info {
  display: flex;
  align-items: center;
  margin-left: 12px;
}

.sidebar-user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  margin-right: 8px;
}

.sidebar-username {
  font-size: 14px;
  font-weight: 500;
  color: #202124;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dark-theme .sidebar-username {
  color: var(--text-primary);
}

.new-chat-btn {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin: 8px 0;
  border: 1px solid #dadce0;
  border-radius: 24px;
  background-color: white;
  color: #202124;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dark-theme .new-chat-btn {
  background-color: var(--bg-secondary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.new-chat-btn svg {
  margin-right: 8px;
}

.new-chat-btn:hover {
  background-color: #f1f3f4;
}

.dark-theme .new-chat-btn:hover {
  background-color: var(--hover-color);
}

.section-header {
  font-size: 14px;
  font-weight: 500;
  color: #5f6368;
  padding: 12px 16px 8px;
}

.dark-theme .section-header {
  color: var(--text-secondary);
}

.sidebar-options {
  margin-bottom: 16px;
}

.sidebar-option {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  cursor: pointer;
  border-radius: 8px;
}

.sidebar-option:hover {
  background-color: #e8eaed;
}

.dark-theme .sidebar-option:hover {
  background-color: var(--hover-color);
}

.option-icon {
  margin-right: 12px;
  font-size: 16px;
}

.option-text {
  font-size: 14px;
}

.dark-theme .option-text {
  color: var(--text-primary);
}

.sidebar-footer {
  padding: 16px;
  margin-top: auto;
  border-top: 1px solid #dadce0;
  display: flex;
  justify-content: center;
}

.dark-theme .sidebar-footer {
  border-top-color: var(--border-color);
}

.settings-button {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: #5f6368;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dark-theme .settings-button {
  color: var(--text-secondary);
}

.settings-button:hover {
  background-color: #e8eaed;
}

.dark-theme .settings-button:hover {
  background-color: var(--hover-color);
}

.settings-text {
  margin-left: 8px;
  font-size: 14px;
}

/* 设置面板 */
.settings-panel {
  position: absolute;
  left: 280px;
  top: 0;
  bottom: 0;
  width: 300px;
  background-color: white;
  border-right: 1px solid #dadce0;
  z-index: 100;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.dark-theme .settings-panel {
  background-color: var(--bg-secondary);
  border-right-color: var(--border-color);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.3);
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #dadce0;
}

.dark-theme .settings-header {
  border-bottom-color: var(--border-color);
}

.settings-header h2 {
  font-size: 18px;
  font-weight: 500;
}

.dark-theme .settings-header h2 {
  color: var(--text-primary);
}

.close-settings {
  background: none;
  border: none;
  cursor: pointer;
  color: #5f6368;
  padding: 4px;
  border-radius: 50%;
}

.dark-theme .close-settings {
  color: var(--text-secondary);
}

.close-settings:hover {
  background-color: #e8eaed;
}

.dark-theme .close-settings:hover {
  background-color: var(--hover-color);
}

.settings-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.settings-section {
  margin-bottom: 24px;
}

.settings-section h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
}

.dark-theme .settings-section h3 {
  color: var(--text-primary);
}

.theme-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dark-theme .theme-toggle {
  color: var(--text-primary);
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #1a73e8;
}

.dark-theme input:checked + .slider {
  background-color: var(--accent-color);
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.ai-avatar-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 16px;
}

.avatar-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 16px;
}

.avatar-option {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.avatar-option:hover {
  border-color: #1a73e8;
}

.avatar-option.selected {
  border-color: #1a73e8;
}

.avatar-option img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.selected-avatar-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 16px;
}

.selected-avatar-preview p {
  font-size: 14px;
  color: #5f6368;
  margin-bottom: 8px;
}

.selected-avatar-preview img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid #1a73e8;
}

.dark-theme .selected-avatar-preview img {
  border-color: var(--accent-color);
}

/* 右侧主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
  position: relative;
}

.dark-theme .main-content {
  background-color: var(--bg-primary);
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #dadce0;
  background-color: white;
}

.dark-theme .main-header {
  background-color: var(--bg-primary);
  border-bottom-color: var(--border-color);
}

.header-left {
  display: flex;
  align-items: center;
}

.gemini-title {
  font-size: 22px;
  font-weight: 500;
  color: #202124;
  margin-right: 16px;
}

.dark-theme .gemini-title {
  color: var(--text-primary);
}

.model-selector {
  display: flex;
  align-items: center;
  background-color: #f1f3f4;
  border-radius: 16px;
  padding: 6px 12px;
  cursor: pointer;
}

.dark-theme .model-selector {
  background-color: var(--bg-secondary);
}

.model-name {
  font-size: 14px;
  margin-right: 4px;
}

.dark-theme .model-name {
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

/* 聊天区域样式 */
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-height: 70vh; /* Add max height */
}

.loading-indicator {
  display: flex;
  gap: 4px;
  align-self: flex-start;
  margin-left: 48px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #9aa0a6;
  animation: pulse 1.5s infinite ease-in-out;
}

.dark-theme .loading-dot {
  background-color: var(--text-secondary);
}

.loading-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes pulse {
  0%, 100% { transform: scale(0.7); opacity: 0.5; }
  50% { transform: scale(1); opacity: 1; }
}

/* 输入区域样式 */
.input-container {
  padding: 16px 24px 24px;
  background-color: #fff;
}

.dark-theme .input-container {
  background-color: var(--bg-primary);
}

.input-wrapper {
  border: 1px solid #dadce0;
  border-radius: 24px;
  background-color: white;
  padding: 8px 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.dark-theme .input-wrapper {
  background-color: var(--bg-secondary);
  border-color: var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.message-input {
  width: 100%;
  min-height: 24px;
  max-height: 150px;
  padding: 8px 0;
  border: none;
  resize: none;
  font-family: inherit;
  font-size: 16px;
  background-color: transparent;
  outline: none;
  color: #202124;
}

.dark-theme .message-input {
  color: var(--text-primary);
}

.message-input::placeholder {
  color: #9aa0a6;
}

.dark-theme .message-input::placeholder {
  color: var(--text-secondary);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #dadce0;
}

.input-actions.no-border {
  border-top: none;
}

.dark-theme .input-actions {
  border-top-color: var(--border-color);
}

.dark-theme .input-actions.no-border {
  border-top: none;
}

.stop-button {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: #5f6368;
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-right: 12px;
}

.dark-theme .stop-button {
  color: var(--text-secondary);
}

.stop-button svg {
  margin-right: 4px;
}

.stop-button:hover {
  background-color: #f1f3f4;
}

.dark-theme .stop-button:hover {
  background-color: var(--hover-color);
}

.send-button {
  background-color: transparent;
  border: none;
  color: #1a73e8;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.dark-theme .send-button {
  color: var(--accent-color);
}

.send-button:hover {
  background-color: #e8f0fe;
}

.dark-theme .send-button:hover {
  background-color: var(--hover-color);
}

.send-button:disabled {
  color: #9aa0a6;
  cursor: not-allowed;
}

.dark-theme .send-button:disabled {
  color: var(--text-secondary);
}
</style>
