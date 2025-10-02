package com.lyhm.aicodehelper.ai;

import com.lyhm.aicodehelper.ai.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

import java.util.List;
@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeHelperService {
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
//    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);
    record Report(String name, List<String> suggestionList){}
    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

    //流式对话
    @SystemMessage(fromResource = "system-prompt.txt")
    Flux<String> chatStream(@MemoryId int memoryid, @UserMessage String message);
}
