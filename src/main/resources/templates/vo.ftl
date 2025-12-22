package ${package}.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ${moduleNameUpper}视图对象
 * 用于接口返回${moduleNameUpper}信息，隐藏敏感字段
 *
 * @author ${author}
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ${moduleNameUpper}VO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ${moduleNameUpper}ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     * 0: 禁用, 1: 启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}