package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, String> {

    @Query("select p.project.id, coalesce(sum(p.likesCount), 0) from CommunityPost p where p.project.id in :projectIds group by p.project.id")
    List<Object[]> sumLikesByProjectIds(@Param("projectIds") List<String> projectIds);

    @Query("select p.project.id, p.tags from CommunityPost p where p.project.id in :projectIds and p.tags is not null")
    List<Object[]> findTagsByProjectIds(@Param("projectIds") List<String> projectIds);
}
