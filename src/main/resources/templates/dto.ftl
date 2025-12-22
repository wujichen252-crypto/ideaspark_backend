package ${package}.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ${moduleNameUpper}创建DTO
 * 用于接收${moduleNameUpper}创建请求的参数
 *
 * @author ${author}
 */
@Data
public class Create${moduleNameUpper}DTO {

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Size(min = 1, max = 50, message = "名称长度必须在1-50个字符之间")
    private String name;

    /**
     * 描述
     */
    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;

    /**
     * 状态
     * 0: 禁用, 1: 启用
     */
    private Integer status;
}