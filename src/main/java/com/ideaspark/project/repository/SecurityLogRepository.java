package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.SecurityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 安全日志数据访问层
 */
@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

    /**
     * 获取用户的安全日志（分页）
     */
    Page<SecurityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取用户特定类型的安全日志
     */
    List<SecurityLog> findByUserIdAndActionTypeOrderByCreatedAtDesc(Long userId, String actionType);

    /**
     * 统计用户在指定时间内的登录次数
     */
    long countByUserIdAndActionTypeAndCreatedAtAfter(Long userId, String actionType, LocalDateTime since);
}
