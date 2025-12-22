package ${package}.controller;

import ${basePackage}.common.response.ApiResponse;
import ${package}.model.dto.Create${moduleNameUpper}DTO;
import ${package}.model.vo.${moduleNameUpper}VO;
import ${package}.service.${moduleNameUpper}Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${moduleNameUpper}控制器
 * 处理${moduleNameUpper}相关的HTTP请求
 *
 * @author ${author}
 */
@RestController
@RequestMapping("/api/${moduleName}s")
public class ${moduleNameUpper}Controller {

    private final ${moduleNameUpper}Service ${moduleName}Service;

    @Autowired
    public ${moduleNameUpper}Controller(${moduleNameUpper}Service ${moduleName}Service) {
        this.${moduleName}Service = ${moduleName}Service;
    }

    /**
     * 创建${moduleNameUpper}
     *
     * @param dto ${moduleNameUpper}创建参数
     * @return ApiResponse<${moduleNameUpper}VO>
     */
    @PostMapping
    public ApiResponse<${moduleNameUpper}VO> create${moduleNameUpper}(@Valid @RequestBody Create${moduleNameUpper}DTO dto) {
        return ApiResponse.success(${moduleName}Service.create${moduleNameUpper}(dto));
    }

    /**
     * 根据ID查询${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return ApiResponse<${moduleNameUpper}VO>
     */
    @GetMapping("/{id}")
    public ApiResponse<${moduleNameUpper}VO> get${moduleNameUpper}ById(@PathVariable Long id) {
        return ${moduleName}Service.get${moduleNameUpper}ById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "${moduleNameUpper}不存在"));
    }

    /**
     * 查询所有${moduleNameUpper}
     *
     * @return ApiResponse<List<${moduleNameUpper}VO>>
     */
    @GetMapping
    public ApiResponse<List<${moduleNameUpper}VO>> getAll${moduleNameUpper}s() {
        return ApiResponse.success(${moduleName}Service.getAll${moduleNameUpper}s());
    }

    /**
     * 更新${moduleNameUpper}状态
     *
     * @param id     ${moduleNameUpper}ID
     * @param status ${moduleNameUpper}状态
     * @return ApiResponse<Boolean>
     */
    @PutMapping("/{id}/status/{status}")
    public ApiResponse<Boolean> update${moduleNameUpper}Status(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = ${moduleName}Service.update${moduleNameUpper}Status(id, status);
        return success ? ApiResponse.success(true) : ApiResponse.error(404, "${moduleNameUpper}不存在");
    }

    /**
     * 删除${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return ApiResponse<Boolean>
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete${moduleNameUpper}(@PathVariable Long id) {
        boolean success = ${moduleName}Service.delete${moduleNameUpper}(id);
        return success ? ApiResponse.success(true) : ApiResponse.error(404, "${moduleNameUpper}不存在");
    }
}