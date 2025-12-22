package ${package}.service;

import ${package}.model.dto.Create${moduleNameUpper}DTO;
import ${package}.model.vo.${moduleNameUpper}VO;

import java.util.List;
import java.util.Optional;

/**
 * ${moduleNameUpper}服务接口
 * 定义${moduleNameUpper}相关的业务方法
 *
 * @author ${author}
 */
public interface ${moduleNameUpper}Service {

    /**
     * 创建${moduleNameUpper}
     *
     * @param dto ${moduleNameUpper}创建参数
     * @return ${moduleNameUpper}VO
     */
    ${moduleNameUpper}VO create${moduleNameUpper}(Create${moduleNameUpper}DTO dto);

    /**
     * 根据ID查询${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return ${moduleNameUpper}VO
     */
    Optional<${moduleNameUpper}VO> get${moduleNameUpper}ById(Long id);

    /**
     * 根据名称查询${moduleNameUpper}
     *
     * @param name 名称
     * @return ${moduleNameUpper}VO
     */
    Optional<${moduleNameUpper}VO> get${moduleNameUpper}ByName(String name);

    /**
     * 查询所有${moduleNameUpper}
     *
     * @return List<${moduleNameUpper}VO>
     */
    List<${moduleNameUpper}VO> getAll${moduleNameUpper}s();

    /**
     * 更新${moduleNameUpper}状态
     *
     * @param id     ${moduleNameUpper}ID
     * @param status ${moduleNameUpper}状态
     * @return boolean
     */
    boolean update${moduleNameUpper}Status(Long id, Integer status);

    /**
     * 删除${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return boolean
     */
    boolean delete${moduleNameUpper}(Long id);
}