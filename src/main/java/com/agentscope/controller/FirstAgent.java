package com.agentscope.controller;

import com.agentscope.tools.WeaterTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.formatter.ollama.OllamaChatFormatter;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.OllamaChatModel;
import io.agentscope.core.model.ollama.OllamaOptions;
import io.agentscope.core.model.ollama.ThinkOption;
import io.agentscope.core.state.InMemoryAgentStateStore;
import io.agentscope.core.state.JsonFileAgentStateStore;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tracing.OtelTracingMiddleware;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;

import java.nio.file.Paths;

public class FirstAgent {
    public static void main(String[] args) {
        agentWithMetaTool();
    }

    /**
     * 默认会调用工具，导致异常
     */
    public static void harnessAgent() {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new WeaterTools());
        HarnessAgent agent = HarnessAgent.builder()
            .name("note-taker")
            .sysPrompt("你是一个帮助用户做笔记的助手。")
            // 字符串形式由 ModelRegistry 解析 —— 自动读取 DASHSCOPE_API_KEY；
            // 切换其他厂商时改用 "openai:gpt-5.5"、"anthropic:claude-sonnet-4-5"、
            // "gemini:gemini-2.0-flash" 或 "ollama:llama3"。
            .model(OllamaChatModel.builder()
                .baseUrl("http://192.168.0.216:11434")
                .modelName("qwen2.5:3b")
                .formatter(new OllamaChatFormatter())
                .defaultOptions(OllamaOptions.builder()
                    .thinkOption(ThinkOption.ThinkBoolean.DISABLED)
                    .build())
                .build()).toolkit(toolkit)
            .enableMetaTool(false)
            .workspace(Paths.get(".agentscope/workspace"))
            .compaction(CompactionConfig.builder()
                .triggerMessages(30)
                .keepMessages(10)
                .build())
            .build();

        RuntimeContext ctx = RuntimeContext.builder()
            .sessionId("demo-session")
            .userId("alice")
            .build();

        // 第一轮：自我介绍 + 当天的事
        /*Msg block = agent.call(new UserMessage("我叫天宇，今天准备一个关于 ReAct 的技术分享。"), ctx).block();
        System.out.println(block.getTextContent());
        // 第二轮：同 sessionId，自动恢复上一轮状态后回答
        Msg block1 = agent.call(new UserMessage("我叫什么？我今天要干什么？"), ctx).block();
        System.out.println(block1.getTextContent());*/
        Msg block2 = agent.call(new UserMessage("西安天气怎么样？"), ctx).block();
        System.out.println(block2.getTextContent());
    }

    public static void agentWithMetaTool() {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new WeaterTools());
        ReActAgent agent = ReActAgent.builder()
            .name("note-taker")
            .sysPrompt("你是一个智能助手，可以回答各种问题。")
            .model("dashscope:qwen3.7-plus")
            // 字符串形式由 ModelRegistry 解析 —— 自动读取 DASHSCOPE_API_KEY；
            // 切换其他厂商时改用 "openai:gpt-5.5"、"anthropic:claude-sonnet-4-5"、
            // "gemini:gemini-2.0-flash" 或 "ollama:llama3"。
            /*.model(OllamaChatModel.builder()
                .baseUrl("http://192.168.0.216:11434")
                .modelName("qwen2.5:3b")
                .formatter(new OllamaChatFormatter())
                .defaultOptions(OllamaOptions.builder()
                    .thinkOption(ThinkOption.ThinkBoolean.ENABLED) //开启思考模式才能调用工具
                    .build())
                .build())*/
            /*.model(DashScopeChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))// 环境变量中配置
                .stream(false)
                .enableThinking(false)
                .modelName("qwen3.7-plus").build())*/
            .defaultSessionId("default_session_id")
            .toolkit(toolkit)
            .middleware(new OtelTracingMiddleware())
            .stateStore(new JsonFileAgentStateStore(Paths.get("./data", "workspace")))
            .build();

        Msg block = agent.call(new UserMessage("西安天气怎么样？")).block();
        System.out.println(block.getTextContent());
    }

}