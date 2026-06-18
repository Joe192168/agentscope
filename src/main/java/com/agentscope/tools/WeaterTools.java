package com.agentscope.tools;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

/**
 * @program: geometry-geodata
 * @description:
 * @author: xqh
 * @create: 2026-06-18 09:42
 **/
public class WeaterTools {

    @Tool(name = "getWeather", description = "获取天气信息")
    public static String getWeather(
        @ToolParam(name = "city", description = "城市名称") String city) {
        return "城市："+city+"天气查询结果，具体结果如下："+"晴转多云";
    }
}
