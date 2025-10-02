package com.lyhm.aicodehelper.ai;


import com.lyhm.aicodehelper.ai.tools.InterviewQuestionTool;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {

    @Resource
    private ChatModel qwenChatModel;
    @Resource
    private ContentRetriever contentRetriever;
    @Bean
    public AiCodeHelperService aiCodeHelperService(McpToolProvider mcpToolProvider, QwenStreamingChatModel qwenStreamingChatModel){
        //会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//保存10条的会话信息
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(memoryId ->MessageWindowChatMemory.withMaxMessages(10))//每个对话独立存储
                .contentRetriever(contentRetriever)//RAG检索增强生成
                .tools(new InterviewQuestionTool()) //工具调用
                .toolProvider(mcpToolProvider) //Mcp调用
                .build();
        return aiCodeHelperService;
    }
}
