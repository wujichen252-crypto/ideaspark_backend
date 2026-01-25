package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findFirstByTeam_UuidOrderByCreatedAtDesc(String teamUuid);
}
