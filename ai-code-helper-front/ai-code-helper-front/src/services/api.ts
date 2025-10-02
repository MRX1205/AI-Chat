import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// API服务
export const apiService = {
  /**
   * 通过SSE方式与AI进行对话
   * @param memoryId 会话ID
   * @param message 用户消息
   * @returns EventSource实例
   */
  chatStream(memoryId: number, message: string): EventSource {
    const url = `${API_BASE_URL}/ai/chat?memoryId=${memoryId}&message=${encodeURIComponent(message)}`;
    return new EventSource(url);
  },
  
  /**
   * 关闭SSE连接
   * @param eventSource 要关闭的EventSource实例
   */
  closeEventSource(eventSource: EventSource): void {
    if (eventSource) {
      eventSource.close();
    }
  }
};

export default apiService; 