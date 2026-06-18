package com.agentscope.config;

import com.agentscope.tools.WeaterTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.state.JsonFileAgentStateStore;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.file.ReadFileTool;
import io.agentscope.core.tool.file.WriteFileTool;
import io.agentscope.core.tracing.OtelTracingMiddleware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

/**
 * @program: agentscope
 * @description:
 * @author: xqh
 * @create: 2026-06-18 11:36
 **/
@Configuration
public class AgentConfig {

    @Bean
    public ReActAgent reActAgent(){
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new WeaterTools());
        toolkit.registerTool(new WriteFileTool("E:\\工作文件\\AI游戏"));
        toolkit.registerTool(new ReadFileTool("E:\\工作文件\\AI游戏"));

        PermissionContextState contextState = PermissionContextState.builder()
            .mode(PermissionMode.DEFAULT).build();

        return ReActAgent.builder()
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
            .permissionContext(contextState)
            .build();
    }

}
