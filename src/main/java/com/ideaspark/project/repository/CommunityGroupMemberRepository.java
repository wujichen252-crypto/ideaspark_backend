package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityGroupMemberRepository extends JpaRepository<CommunityGroupMember, String> {

    /**
     * 根据圈子ID和用户ID查询成员记录
     */
    Optional<CommunityGroupMember> findByGroupIdAndUserId(String groupId, Long userId);

    /**
     * 根据圈子ID查询所有成员
     */
    List<CommunityGroupMember> findByGroupId(String groupId);

    /**
     * 根据用户ID查询加入的所有圈子
     */
    List<CommunityGroupMember> findByUserId(Long userId);

    /**
     * 统计圈子的成员数
     */
    long countByGroupId(String groupId);

    /**
     * 检查用户是否在圈子中
     */
    boolean existsByGroupIdAndUserId(String groupId, Long userId);

    /**
     * 删除成员记录
     */
    @Modifying
    @Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM CommunityGroupMember cgm WHERE cgm.group.id = :groupId AND cgm.user.id = :userId")
    void deleteByGroupIdAndUserId(@org.springframework.data.repository.query.Param("groupId") String groupId, @org.springframework.data.repository.query.Param("userId") Long userId);
}
