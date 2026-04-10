package com.ideaspark.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    /**
     * 帖子数量
     */
    private Long postCount;

    /**
     * 获赞总数
     */
    private Long totalLikesReceived;

    /**
     * 项目数量
     */
    private Long projectCount;

    /**
     * 关注数
     */
    private Long followingCount;

    /**
     * 粉丝数
     */
    private Long followerCount;
}
