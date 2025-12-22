package ${package}.service.impl;

import ${basePackage}.common.exception.BusinessException;
import ${package}.model.dto.Create${moduleNameUpper}DTO;
import ${package}.model.entity.${moduleNameUpper}Entity;
import ${package}.model.vo.${moduleNameUpper}VO;
import ${package}.repository.${moduleNameUpper}Repository;
import ${package}.service.${moduleNameUpper}Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ${moduleNameUpper}服务实现类
 * 实现${moduleNameUpper}相关的业务逻辑
 *
 * @author ${author}
 */
@Service
public class ${moduleNameUpper}ServiceImpl implements ${moduleNameUpper}Service {

    private final ${moduleNameUpper}Repository ${moduleName}Repository;

    @Autowired
    public ${moduleNameUpper}ServiceImpl(${moduleNameUpper}Repository ${moduleName}Repository) {
        this.${moduleName}Repository = ${moduleName}Repository;
    }

    /**
     * 创建${moduleNameUpper}
     *
     * @param dto ${moduleNameUpper}创建参数
     * @return ${moduleNameUpper}VO
     */
    @Override
    @Transactional
    public ${moduleNameUpper}VO create${moduleNameUpper}(Create${moduleNameUpper}DTO dto) {
        // 检查名称是否已存在
        if (${moduleName}Repository.existsByName(dto.getName())) {
            throw new BusinessException(400, "名称已存在");
        }

        // 创建${moduleNameUpper}实体
        ${moduleNameUpper}Entity ${moduleName}Entity = new ${moduleNameUpper}Entity();
        BeanUtils.copyProperties(dto, ${moduleName}Entity);
        ${moduleName}Entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1); // 默认启用状态

        // 保存${moduleNameUpper}
        ${moduleName}Entity = ${moduleName}Repository.save(${moduleName}Entity);

        // 转换为VO返回
        return convertToVO(${moduleName}Entity);
    }

    /**
     * 根据ID查询${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return ${moduleNameUpper}VO
     */
    @Override
    public Optional<${moduleNameUpper}VO> get${moduleNameUpper}ById(Long id) {
        return ${moduleName}Repository.findById(id)
                .map(this::convertToVO);
    }

    /**
     * 根据名称查询${moduleNameUpper}
     *
     * @param name 名称
     * @return ${moduleNameUpper}VO
     */
    @Override
    public Optional<${moduleNameUpper}VO> get${moduleNameUpper}ByName(String name) {
        return ${moduleName}Repository.findByName(name)
                .map(this::convertToVO);
    }

    /**
     * 查询所有${moduleNameUpper}
     *
     * @return List<${moduleNameUpper}VO>
     */
    @Override
    public List<${moduleNameUpper}VO> getAll${moduleNameUpper}s() {
        return ${moduleName}Repository.findAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 更新${moduleNameUpper}状态
     *
     * @param id     ${moduleNameUpper}ID
     * @param status ${moduleNameUpper}状态
     * @return boolean
     */
    @Override
    @Transactional
    public boolean update${moduleNameUpper}Status(Long id, Integer status) {
        return ${moduleName}Repository.findById(id)
                .map(${moduleName} -> {
                    ${moduleName}.setStatus(status);
                    ${moduleName}Repository.save(${moduleName});
                    return true;
                })
                .orElse(false);
    }

    /**
     * 删除${moduleNameUpper}
     *
     * @param id ${moduleNameUpper}ID
     * @return boolean
     */
    @Override
    @Transactional
    public boolean delete${moduleNameUpper}(Long id) {
        if (${moduleName}Repository.existsById(id)) {
            ${moduleName}Repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 将${moduleNameUpper}实体转换为VO
     *
     * @param ${moduleName}Entity ${moduleNameUpper}实体
     * @return ${moduleNameUpper}VO
     */
    private ${moduleNameUpper}VO convertToVO(${moduleNameUpper}Entity ${moduleName}Entity) {
        ${moduleNameUpper}VO ${moduleName}VO = new ${moduleNameUpper}VO();
        BeanUtils.copyProperties(${moduleName}Entity, ${moduleName}VO);
        return ${moduleName}VO;
    }
}