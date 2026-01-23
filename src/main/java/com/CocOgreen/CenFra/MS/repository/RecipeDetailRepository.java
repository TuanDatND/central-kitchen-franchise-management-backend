package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.RecipeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeDetailRepository extends JpaRepository<RecipeDetail, Integer> {
}
