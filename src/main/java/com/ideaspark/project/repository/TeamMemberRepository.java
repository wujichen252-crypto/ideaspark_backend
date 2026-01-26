package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.TeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Page<TeamMember> findByUser_Id(Long userId, Pageable pageable);

    Optional<TeamMember> findByTeam_UuidAndUser_Id(String teamUuid, Long userId);

    long countByTeam_Uuid(String teamUuid);

    Page<TeamMember> findByTeam_Uuid(String teamUuid, Pageable pageable);

    Page<TeamMember> findByTeam_UuidAndRole(String teamUuid, String role, Pageable pageable);

    Page<TeamMember> findByTeam_UuidAndUser_UsernameContainingIgnoreCase(String teamUuid, String keyword, Pageable pageable);

    Page<TeamMember> findByTeam_UuidAndRoleAndUser_UsernameContainingIgnoreCase(String teamUuid, String role, String keyword, Pageable pageable);
}
