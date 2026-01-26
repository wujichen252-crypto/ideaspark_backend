package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, String> {
}
