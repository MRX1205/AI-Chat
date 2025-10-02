package com.lyhm.aicodehelper;



import com.lyhm.aicodehelper.ai.AiCodeHelperService;
import com.lyhm.aicodehelper.ai.AiCodeHleper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperApplicationTests {

	@Resource
    private AiCodeHleper aiCodeHleper;

//    @Test
//    void chat() {
//        aiCodeHleper.chat("您好，我是掠影航猫");
//    }我是掠影航猫

    @Test
    void chatwithmessage() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("你好 帮我描述我发给你的图片！"),
                ImageContent.from("https://i2.hdslb.com/bfs/face/86e120afca85ac488259406dc5134afea8d3643b.jpg@240w_240h_1c_1s_!web-avatar-nav")
        );
        aiCodeHleper.chatwithmessage(userMessage);
    }
    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好，我是掠影航猫");
        System.out.println(result);
    }
    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好，我是掠影航猫");
        System.out.println(result);
        result = aiCodeHelperService.chat("我是谁来着？");
        System.out.println(result);
    }
    @Test
    void chatWithRag() {
        String result = aiCodeHelperService.chat("怎么学习java");
        System.out.println(result);
    }
}
