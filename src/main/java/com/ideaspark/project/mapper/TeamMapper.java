package com.ideaspark.project.mapper;

import com.ideaspark.project.model.dto.response.*;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.TeamInvitation;
import com.ideaspark.project.model.entity.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 团队对象映射器
 * @description 使用 MapStruct 实现 Team 相关实体与 DTO 之间的转换
 * @author IdeaSpark
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeamMapper {

    /**
     * 将 Team 实体转换为 TeamListItemResponse DTO
     * @param team 团队实体
     * @return 团队列表项响应 DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    TeamListItemResponse toTeamListItemResponse(Team team);

    /**
     * 将 Team 实体转换为 TeamDetailResponse DTO
     * @param team 团队实体
     * @return 团队详情响应 DTO
     */
    TeamDetailResponse toTeamDetailResponse(Team team);

    /**
     * 将 TeamMember 实体转换为 TeamMemberListItemResponse DTO
     * @param member 团队成员实体
     * @return 团队成员列表项响应 DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "userAvatar", source = "user.avatar")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "joinedAt", source = "joinedAt")
    TeamMemberListItemResponse toTeamMemberListItemResponse(TeamMember member);

    /**
     * 将 TeamMember 实体列表转换为 TeamMemberListItemResponse DTO 列表
     * @param members 团队成员实体列表
     * @return 团队成员列表项响应 DTO 列表
     */
    List<TeamMemberListItemResponse> toTeamMemberListItemResponseList(List<TeamMember> members);

    /**
     * 将 TeamInvitation 实体转换为 TeamInvitationItemResponse DTO
     * @param invitation 团队邀请实体
     * @return 团队邀请项响应 DTO
     */
    @Mapping(target = "inviteeId", source = "invitee.id")
    @Mapping(target = "inviteeEmail", source = "invitee.email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresAt", source = "expiresAt")
    TeamInvitationItemResponse toTeamInvitationItemResponse(TeamInvitation invitation);

    /**
     * 将 TeamInvitation 实体列表转换为 TeamInvitationItemResponse DTO 列表
     * @param invitations 团队邀请实体列表
     * @return 团队邀请项响应 DTO 列表
     */
    List<TeamInvitationItemResponse> toTeamInvitationItemResponseList(List<TeamInvitation> invitations);
}
