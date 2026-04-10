package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 项目成员 Repository
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, String> {

    /**
     * 根据项目ID查询成员列表
     */
    List<ProjectMember> findByProject_Id(String projectId);

    /**
     * 根据项目ID和用户ID查询成员
     */
    Optional<ProjectMember> findByProject_IdAndUser_Id(String projectId, Long userId);

    /**
     * 检查用户是否是项目成员
     */
    boolean existsByProject_IdAndUser_Id(String projectId, Long userId);

    /**
     * 删除项目成员
     */
    void deleteByProject_IdAndUser_Id(String projectId, Long userId);
}
