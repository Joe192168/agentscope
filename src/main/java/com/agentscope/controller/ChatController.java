package com.agentscope.controller;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 多用户多对话并发请求
 */
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ReActAgent agent;

    /**
     * 测试例子：
     * http://localhost:8080/chat?userid=1&sessionId=1&message=我喜欢踢足球
     * http://localhost:8080/chat?userid=1&sessionId=2&message=我喜欢踢足球
     * http://localhost:8080/chat?userid=1&sessionId=3&message=我喜欢看电影
     * 提问例子：
     * http://localhost:8080/chat?userid=1&sessionId=1&message=我喜欢什么
     * http://localhost:8080/chat?userid=1&sessionId=2&message=我喜欢什么
     * http://localhost:8080/chat?userid=1&sessionId=3&message=我喜欢什么
     * @param userId
     * @param sessionId
     * @param message
     * @return
     */
    @GetMapping(value = "/chat",produces = MediaType.TEXT_PLAIN_VALUE+";charset=UTF-8")
    public Flux<String> chat(String userId, String sessionId, String message){
        return agent.streamEvents(
            new UserMessage(message),
            RuntimeContext.builder().userId(userId).sessionId(sessionId).build()
        ).filter(event-> event instanceof TextBlockDeltaEvent)
            .map(event-> ((TextBlockDeltaEvent) event).getDelta());
    }

    /**
     * 中断请求
     *
     * 测试例子：
     * http://localhost:8080/chat?userid=1&sessionId=1&message=请详细解释一下什么是量子力学
     *
     * 中断例子：
     * http://localhost:8080/intercept?userid=1&sessionId=1
     * @param userId
     * @param sessionId
     */
    @GetMapping(value = "/intercept",produces = MediaType.TEXT_PLAIN_VALUE+";charset=UTF-8")
    public void intercept(String userId, String sessionId){
        agent.interrupt(
            RuntimeContext.builder()
                .userId(userId)
                .sessionId(sessionId).build(),
                new UserMessage("用户已取消"));
    }

}
