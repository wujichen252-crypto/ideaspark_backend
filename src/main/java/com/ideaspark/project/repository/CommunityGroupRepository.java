package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityGroupRepository extends JpaRepository<CommunityGroup, String> {

    /**
     * 根据名称查询圈子
     */
    Optional<CommunityGroup> findByName(String name);

    /**
     * 检查圈子名称是否已存在
     */
    boolean existsByName(String name);
}
