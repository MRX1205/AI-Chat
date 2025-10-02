package com.lyhm.aicodehelper.ai;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.AbstractDocument;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiCodeHelperServiceTest {
    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Resource
    private AiCodeHleper aiCodeHleper;
    @Test
    void chatForReport() {
        String userMessage = "您好，我是掠影航猫，学java一年了，请帮我制定学习报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }
    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习java");
        String content = result.content();
        List<Content> sources = result.sources();
        System.out.println(content);
        System.out.println(sources);
    }
    @Test
    void chatWithTools(){
        String result = aiCodeHelperService.chat("有哪些常见的计算机网络面试题");
        System.out.println(result);
    }
    @Test
    void chatWithMcp(){
        String result = aiCodeHelperService.chat("什么是程序员鱼皮的编程导航");
        System.out.println(result);
    }
    @Test
    void chatWithGuardrail(){
        String result = aiCodeHelperService.chat(" the game");
        System.out.println(result);
    }
}