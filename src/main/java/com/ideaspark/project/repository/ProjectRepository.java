package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findFirstByTeam_UuidOrderByCreatedAtDesc(String teamUuid);

    @Query("select p from Project p where p.visibility = 'public' and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%'))) and (:category is null or p.category = :category)")
    Page<Project> searchMarketProjects(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    Optional<Project> findByIdAndVisibility(String id, String visibility);
}
