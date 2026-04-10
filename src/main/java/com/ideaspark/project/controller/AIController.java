package com.ideaspark.project.controller;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.DeepSeekChatRequest;
import com.ideaspark.project.model.dto.response.DeepSeekChatResponse;
import com.ideaspark.project.service.DeepSeekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 功能控制器
 * 提供 AI 聊天、项目生成等功能接口
 * 接入 DeepSeek API
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI功能", description = "AI聊天、项目生成等功能接口（DeepSeek）")
public class AIController {

    private final DeepSeekService deepSeekService;

    /**
     * AI 聊天
     */
    @PostMapping("/chat")
    @Operation(summary = "AI聊天", description = "发送消息给 AI 助手并获取回复")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        if (messages == null || messages.isEmpty()) {
            throw new BusinessException("消息不能为空");
        }

        // 转换消息格式
        List<DeepSeekChatRequest.Message> deepSeekMessages = messages.stream()
                .map(msg -> DeepSeekChatRequest.Message.builder()
                        .role(msg.get("role"))
                        .content(msg.get("content"))
                        .build())
                .toList();

        // 调用 DeepSeek API
        DeepSeekChatResponse response = deepSeekService.chat(deepSeekMessages);
        DeepSeekChatResponse.Message assistantMessage = response.getChoices().get(0).getMessage();

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Success",
                "data", Map.of(
                        "message", Map.of(
                                "role", assistantMessage.getRole(),
                                "content", assistantMessage.getContent(),
                                "timestamp", System.currentTimeMillis()
                        ),
                        "usage", response.getUsage()
                )
        ));
    }

    /**
     * 简单对话（便捷接口）
     */
    @PostMapping("/chat/simple")
    @Operation(summary = "简单对话", description = "单条消息对话，无需维护上下文")
    public ResponseEntity<?> chatSimple(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException("消息不能为空");
        }

        String response = deepSeekService.chat(message);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Success",
                "data", Map.of(
                        "content", response,
                        "timestamp", System.currentTimeMillis()
                )
        ));
    }

    /**
     * 生成项目方案
     */
    @PostMapping("/generate-project")
    @Operation(summary = "生成项目方案", description = "根据描述生成完整的项目方案")
    public ResponseEntity<?> generateProject(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new BusinessException("项目描述不能为空");
        }

        String response = deepSeekService.generateProject(prompt);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Success",
                "data", Map.of(
                        "content", response,
                        "timestamp", System.currentTimeMillis()
                )
        ));
    }

    /**
     * 技术选型建议
     */
    @PostMapping("/tech-advice")
    @Operation(summary = "技术选型建议", description = "获取技术选型建议")
    public ResponseEntity<?> techAdvice(@RequestBody Map<String, String> request) {
        String requirements = request.get("requirements");
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new BusinessException("需求描述不能为空");
        }

        String response = deepSeekService.techStackAdvice(requirements);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Success",
                "data", Map.of(
                        "content", response,
                        "timestamp", System.currentTimeMillis()
                )
        ));
    }

    /**
     * 获取支持的模型列表
     */
    @GetMapping("/models")
    @Operation(summary = "获取模型列表", description = "获取支持的 AI 模型列表")
    public ResponseEntity<?> getModels() {
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Success",
                "data", List.of(
                        Map.of(
                                "id", "deepseek-chat",
                                "name", "DeepSeek-V3",
                                "description", "通用对话模型，适用于大多数场景"
                        ),
                        Map.of(
                                "id", "deepseek-reasoner",
                                "name", "DeepSeek-R1",
                                "description", "推理模型，适用于复杂问题求解"
                        )
                )
        ));
    }

    /**
     * 检查 AI 服务状态
     */
    @GetMapping("/status")
    @Operation(summary = "服务状态", description = "检查 AI 服务是否可用")
    public ResponseEntity<?> status() {
        try {
            // 简单测试调用
            deepSeekService.chat("Hello");
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "AI 服务正常",
                    "data", Map.of("available", true)
            ));
        } catch (Exception e) {
            log.warn("AI 服务检查失败", e);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "AI 服务暂时不可用: " + e.getMessage(),
                    "data", Map.of("available", false)
            ));
        }
    }
}
