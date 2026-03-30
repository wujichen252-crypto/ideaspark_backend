package com.ideaspark.project.mapper;

import com.ideaspark.project.model.dto.response.UserResponse;
import com.ideaspark.project.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * 用户对象映射器
 * @description 使用 MapStruct 实现 User 实体与 DTO 之间的转换
 * @author IdeaSpark
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * 将 User 实体转换为 UserResponse DTO
     * @param user 用户实体
     * @return 用户响应 DTO
     */
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToCn")
    UserResponse toUserResponse(User user);

    /**
     * 将 User 实体列表转换为 UserResponse DTO 列表
     * @param users 用户实体列表
     * @return 用户响应 DTO 列表
     */
    List<UserResponse> toUserResponseList(List<User> users);

    /**
     * 角色编码转中文
     * @param role 角色编码
     * @return 角色中文名称
     */
    @Named("roleToCn")
    default String roleToCn(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "超级管理员";
        }
        if ("USER".equalsIgnoreCase(role)) {
            return "普通用户";
        }
        return role;
    }
}
