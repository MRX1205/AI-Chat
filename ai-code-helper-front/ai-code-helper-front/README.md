# AI 编程小助手前端

这是一个基于Vue3的前端应用，提供了一个聊天界面，让用户可以与AI编程助手进行交流，解答编程学习和求职面试相关的问题。

## 功能特点

- 聊天室风格的界面，支持用户与AI的对话
- 自动生成会话ID，区分不同的对话
- 使用SSE技术实现实时流式响应
- 响应式设计，适配不同屏幕尺寸
- 可自定义AI头像
- 深色/浅色主题切换

## 技术栈

- Vue 3
- TypeScript
- Axios
- Server-Sent Events (SSE)

## 开发环境设置

1. 克隆项目到本地

```bash
git clone <仓库地址>
cd ai-code-helper-front
```

2. 安装依赖

```bash
npm install
```

3. 启动开发服务器

```bash
npm run dev
```

4. 打开浏览器访问 `http://localhost:5173`

## 自定义 AI 头像

AI 头像存储在 `public/avatars/` 目录下。默认提供以下头像文件：

- `default-ai.png` - 默认头像
- `ai-avatar-1.png` - 备选头像 1
- `ai-avatar-2.png` - 备选头像 2
- `ai-avatar-3.png` - 备选头像 3

### 如何替换头像

开发者可以直接替换这些文件来自定义 AI 头像。请确保：

1. 保持相同的文件名
2. 使用方形图片以获得最佳效果
3. 推荐尺寸：200x200 像素或更高
4. 支持的格式：PNG, JPG, SVG

替换文件后，无需额外配置，应用将自动使用新的头像。

## 构建生产版本

```bash
npm run build
```

## 后端API

项目需要配合后端API使用，后端接口地址前缀为：`http://localhost:8081/api`

### 主要接口

- 聊天接口 (SSE): `GET /ai/chat?memoryId={memoryId}&message={message}`
  - `memoryId`: 会话ID
  - `message`: 用户消息

## 项目结构

```
src/
├── assets/         # 静态资源
├── components/     # 组件
├── services/       # 服务层，包含API交互逻辑
├── App.vue         # 主应用组件
└── main.ts         # 应用入口
public/
└── avatars/        # AI 头像目录
    ├── default-ai.png
    ├── ai-avatar-1.png
    ├── ai-avatar-2.png
    └── ai-avatar-3.png
```

## 开发者

这个项目由 [开发者姓名] 开发。
