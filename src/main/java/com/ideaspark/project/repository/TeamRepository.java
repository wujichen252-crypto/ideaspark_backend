package com.ideaspark.project.repository;
import com.ideaspark.project.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {

    boolean existsByName(String name);
}
