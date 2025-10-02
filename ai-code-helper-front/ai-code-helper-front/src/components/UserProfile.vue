<script setup lang="ts">
import { ref, defineEmits, defineExpose } from 'vue'

const props = defineProps<{
  isLoggedIn: boolean
}>()

const emit = defineEmits<{
  (e: 'login'): void
  (e: 'logout'): void
}>()

const showLoginModal = ref(false)
const username = ref('')
const password = ref('')
const nickname = ref('用户')
const userAvatar = ref('https://via.placeholder.com/40')
const showUserMenu = ref(false)
const showEditProfile = ref(false)

const handleLoginClick = () => {
  showLoginModal.value = true
}

const handleLogin = () => {
  if (username.value && password.value) {
    showLoginModal.value = false
    emit('login')
    // 清空输入
    username.value = ''
    password.value = ''
  }
}

const handleLogout = () => {
  showUserMenu.value = false
  emit('logout')
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const showEditProfileModal = () => {
  showUserMenu.value = false
  showEditProfile.value = true
}

const saveProfile = () => {
  showEditProfile.value = false
}

const uploadAvatar = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      userAvatar.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 暴露属性给父组件
defineExpose({
  nickname,
  userAvatar
})
</script>

<template>
  <div class="user-profile-container">
    <div v-if="!isLoggedIn" class="login-section">
      <button @click="handleLoginClick" class="login-btn">登录</button>
    </div>
    <div v-else class="user-section" @click="toggleUserMenu">
      <div class="user-avatar">
        <img :src="userAvatar" alt="用户头像" />
      </div>
    </div>
    
    <!-- 登录弹窗 -->
    <div v-if="showLoginModal" class="login-modal-overlay">
      <div class="login-modal">
        <div class="login-header">
          <h2>登录到 掠影航猫</h2>
          <button class="close-btn" @click="showLoginModal = false">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="form-group">
          <label for="username">电子邮箱或电话</label>
          <input type="text" id="username" v-model="username" placeholder="请输入邮箱或电话" />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" placeholder="请输入密码" />
        </div>
        <div class="modal-actions">
          <button class="forgot-btn">忘记密码?</button>
          <div class="action-buttons">
            <button class="create-account">创建账号</button>
            <button @click="handleLogin" class="login-confirm-btn" :disabled="!username || !password">登录</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 用户下拉菜单 -->
    <div v-if="isLoggedIn && showUserMenu" class="user-dropdown">
      <div class="dropdown-header">
        <div class="user-info">
          <img :src="userAvatar" alt="用户头像" />
          <div class="user-details">
            <div class="user-name">{{ nickname }}</div>
            <div class="user-email">user@example.com</div>
          </div>
        </div>
      </div>
      <div class="dropdown-menu">
        <div class="menu-item" @click="showEditProfileModal">修改个人信息</div>
        <div class="menu-item">账号设置</div>
        <div class="menu-item">帮助中心</div>
        <div class="menu-item" @click="handleLogout">退出登录</div>
      </div>
    </div>
    
    <!-- 编辑个人信息 -->
    <div v-if="showEditProfile" class="edit-profile-overlay">
      <div class="edit-profile-modal">
        <div class="modal-header">
          <h3>编辑个人信息</h3>
          <button class="close-btn" @click="showEditProfile = false">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="modal-content">
          <div class="avatar-section">
            <div class="current-avatar">
              <img :src="userAvatar" alt="当前头像" />
            </div>
            <label for="avatar-upload" class="upload-btn">更换头像</label>
            <input type="file" id="avatar-upload" accept="image/*" @change="uploadAvatar" class="hidden-input" />
          </div>
          
          <div class="form-group">
            <label for="nickname">昵称</label>
            <input type="text" id="nickname" v-model="nickname" placeholder="输入昵称" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showEditProfile = false" class="cancel-btn">取消</button>
          <button @click="saveProfile" class="save-btn">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-profile-container {
  position: relative;
}

.login-section, .user-section {
  display: flex;
  align-items: center;
}

.login-btn {
  padding: 8px 16px;
  border: 1px solid #dadce0;
  border-radius: 4px;
  background-color: #1a73e8;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-btn:hover {
  background-color: #1765cc;
}

.user-section {
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e8f0fe;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 登录弹窗样式 */
.login-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.login-modal {
  background-color: white;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.login-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #dadce0;
}

.login-header h2 {
  font-size: 22px;
  font-weight: 400;
  color: #202124;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #5f6368;
  padding: 4px;
}

.form-group {
  padding: 16px 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #5f6368;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #dadce0;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:focus {
  outline: none;
  border-color: #1a73e8;
}

.modal-actions {
  padding: 16px 24px 24px;
}

.forgot-btn {
  background: none;
  border: none;
  color: #1a73e8;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.create-account {
  background: none;
  border: none;
  color: #1a73e8;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 10px;
}

.login-confirm-btn {
  padding: 10px 24px;
  background-color: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.login-confirm-btn:disabled {
  background-color: #9aa0a6;
  cursor: not-allowed;
}

/* 用户下拉菜单 */
.user-dropdown {
  position: absolute;
  top: 50px;
  right: 0;
  width: 300px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.dropdown-header {
  padding: 16px;
  border-bottom: 1px solid #dadce0;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #202124;
}

.user-email {
  font-size: 14px;
  color: #5f6368;
}

.dropdown-menu {
  padding: 8px 0;
}

.menu-item {
  padding: 12px 16px;
  font-size: 14px;
  color: #202124;
  cursor: pointer;
}

.menu-item:hover {
  background-color: #f1f3f4;
}

/* 编辑个人信息弹窗 */
.edit-profile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.edit-profile-modal {
  background-color: white;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #dadce0;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 500;
  color: #202124;
}

.modal-content {
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.current-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 12px;
  border: 2px solid #1a73e8;
}

.current-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-btn {
  background-color: #1a73e8;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.hidden-input {
  display: none;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
  border-top: 1px solid #dadce0;
}

.cancel-btn {
  background: none;
  border: 1px solid #dadce0;
  color: #5f6368;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  margin-right: 12px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #f1f3f4;
}

.save-btn {
  background-color: #1a73e8;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.save-btn:hover {
  background-color: #1765cc;
}
</style> 