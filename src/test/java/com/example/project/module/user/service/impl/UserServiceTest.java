package com.example.project.module.user.service.impl;

import com.example.project.common.exception.BusinessException;
import com.example.project.module.user.model.dto.CreateUserDTO;
import com.example.project.module.user.model.entity.UserEntity;
import com.example.project.module.user.model.vo.UserVO;
import com.example.project.module.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试用户服务的核心功能
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setNickname("测试用户");
        dto.setEmail("test@example.com");
        dto.setPhone("13800138000");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(dto, userEntity);
        userEntity.setId(1L);
        userEntity.setStatus(1);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        // 模拟Repository行为
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(dto.getPhone())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // 执行测试
        UserVO result = userService.createUser(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(dto.getUsername(), result.getUsername());
        assertEquals(dto.getNickname(), result.getNickname());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getPhone(), result.getPhone());
        assertEquals(1, result.getStatus());

        // 验证Repository方法被调用
        verify(userRepository, times(1)).existsByUsername(dto.getUsername());
        verify(userRepository, times(1)).existsByEmail(dto.getEmail());
        verify(userRepository, times(1)).existsByPhone(dto.getPhone());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_usernameExists_shouldThrowException() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("existinguser");
        dto.setPassword("password123");

        // 模拟Repository行为
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(dto);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());

        // 验证Repository方法被调用
        verify(userRepository, times(1)).existsByUsername(dto.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).existsByPhone(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getUserById_success() {
        // 准备测试数据
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testuser");
        userEntity.setNickname("测试用户");
        userEntity.setEmail("test@example.com");
        userEntity.setStatus(1);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        // 模拟Repository行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // 执行测试
        Optional<UserVO> result = userService.getUserById(userId);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals(userEntity.getUsername(), result.get().getUsername());
        assertEquals(userEntity.getNickname(), result.get().getNickname());
        assertEquals(userEntity.getEmail(), result.get().getEmail());

        // 验证Repository方法被调用
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_notFound_shouldReturnEmpty() {
        // 准备测试数据
        Long userId = 999L;

        // 模拟Repository行为
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 执行测试
        Optional<UserVO> result = userService.getUserById(userId);

        // 验证结果
        assertFalse(result.isPresent());

        // 验证Repository方法被调用
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserStatus_success() {
        // 准备测试数据
        Long userId = 1L;
        Integer newStatus = 0;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testuser");
        userEntity.setStatus(1);

        // 模拟Repository行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // 执行测试
        boolean result = userService.updateUserStatus(userId, newStatus);

        // 验证结果
        assertTrue(result);
        assertEquals(newStatus, userEntity.getStatus());

        // 验证Repository方法被调用
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateUserStatus_notFound_shouldReturnFalse() {
        // 准备测试数据
        Long userId = 999L;
        Integer newStatus = 0;

        // 模拟Repository行为
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 执行测试
        boolean result = userService.updateUserStatus(userId, newStatus);

        // 验证结果
        assertFalse(result);

        // 验证Repository方法被调用
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_success() {
        // 准备测试数据
        Long userId = 1L;

        // 模拟Repository行为
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // 执行测试
        boolean result = userService.deleteUser(userId);

        // 验证结果
        assertTrue(result);

        // 验证Repository方法被调用
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_notFound_shouldReturnFalse() {
        // 准备测试数据
        Long userId = 999L;

        // 模拟Repository行为
        when(userRepository.existsById(userId)).thenReturn(false);

        // 执行测试
        boolean result = userService.deleteUser(userId);

        // 验证结果
        assertFalse(result);

        // 验证Repository方法被调用
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

}