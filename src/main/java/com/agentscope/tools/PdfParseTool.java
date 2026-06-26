package com.agentscope.tools;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.util.Map;

public class PdfParseTool {

    @Tool(name = "getWeather", description = "获取天气信息")
    public Object run(
        @ToolParam(name = "city", description = "城市名称")Map<String, Object> args) {
        return "PDF 内容解析完成（示例）";
    }
}