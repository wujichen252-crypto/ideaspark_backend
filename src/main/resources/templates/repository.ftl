package ${package}.repository;

import ${package}.model.entity.${moduleNameUpper}Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ${moduleNameUpper}数据访问层
 * 负责${moduleNameUpper}实体的数据库操作
 *
 * @author ${author}
 */
@Repository
public interface ${moduleNameUpper}Repository extends JpaRepository<${moduleNameUpper}Entity, Long> {

    /**
     * 根据名称查询${moduleNameUpper}
     *
     * @param name 名称
     * @return Optional<${moduleNameUpper}Entity>
     */
    Optional<${moduleNameUpper}Entity> findByName(String name);

    /**
     * 判断名称是否存在
     *
     * @param name 名称
     * @return boolean
     */
    boolean existsByName(String name);
}