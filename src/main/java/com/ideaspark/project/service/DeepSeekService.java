package com.ideaspark.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaspark.project.config.DeepSeekConfig;
import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.DeepSeekChatRequest;
import com.ideaspark.project.model.dto.response.DeepSeekChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * DeepSeek AI 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;

    /**
     * 发送聊天消息
     */
    public DeepSeekChatResponse chat(List<DeepSeekChatRequest.Message> messages) {
        if (deepSeekConfig.getApiKey() == null || deepSeekConfig.getApiKey().trim().isEmpty()) {
            throw new BusinessException("DeepSeek API Key 未配置");
        }

        try {
            // 构建请求体
            DeepSeekChatRequest request = DeepSeekChatRequest.builder()
                    .model(deepSeekConfig.getModel())
                    .messages(messages)
                    .temperature(deepSeekConfig.getTemperature())
                    .maxTokens(deepSeekConfig.getMaxTokens())
                    .stream(false)
                    .build();

            // 创建 RestClient
            RestClient restClient = RestClient.builder()
                    .baseUrl(deepSeekConfig.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + deepSeekConfig.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            // 发送请求
            DeepSeekChatResponse response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String errorBody = new String(res.getBody().readAllBytes());
                        log.error("DeepSeek API 调用失败: {}", errorBody);
                        throw new BusinessException("AI 服务调用失败: " + res.getStatusCode());
                    })
                    .body(DeepSeekChatResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new BusinessException("AI 响应为空");
            }

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败", e);
            throw new BusinessException("AI 服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 单条消息对话（便捷方法）
     */
    public String chat(String userMessage) {
        List<DeepSeekChatRequest.Message> messages = List.of(
                DeepSeekChatRequest.Message.builder()
                        .role("system")
                        .content("你是 IdeaSpark AI 助手，一个专业的项目管理助手。你可以帮助用户解答项目开发、技术选型、团队协作等问题。请用中文回答，回答要专业、简洁、实用。")
                        .build(),
                DeepSeekChatRequest.Message.builder()
                        .role("user")
                        .content(userMessage)
                        .build()
        );

        DeepSeekChatResponse response = chat(messages);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 生成项目方案
     */
    public String generateProject(String prompt) {
        String systemPrompt = """
                你是一个专业的项目顾问，擅长根据用户需求生成完整的项目方案。
                
                请根据用户的描述，生成包含以下内容的项目方案：
                1. 项目名称（简洁、有创意）
                2. 项目简介（100字左右）
                3. 核心功能列表（3-5个）
                4. 推荐技术栈
                5. 开发周期预估
                6. 关键里程碑
                
                请用 Markdown 格式输出，确保内容专业、实用、可执行。
                """;

        List<DeepSeekChatRequest.Message> messages = List.of(
                DeepSeekChatRequest.Message.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build(),
                DeepSeekChatRequest.Message.builder()
                        .role("user")
                        .content("请帮我生成一个项目方案，需求如下：\n" + prompt)
                        .build()
        );

        DeepSeekChatResponse response = chat(messages);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 技术选型建议
     */
    public String techStackAdvice(String requirements) {
        String systemPrompt = """
                你是一个资深技术架构师，擅长为项目提供技术选型建议。
                
                请根据用户需求，提供：
                1. 推荐的技术栈（前端、后端、数据库、中间件等）
                2. 选择理由
                3. 备选方案
                4. 注意事项
                
                请用中文回答，专业且易于理解。
                """;

        List<DeepSeekChatRequest.Message> messages = List.of(
                DeepSeekChatRequest.Message.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build(),
                DeepSeekChatRequest.Message.builder()
                        .role("user")
                        .content(requirements)
                        .build()
        );

        DeepSeekChatResponse response = chat(messages);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
