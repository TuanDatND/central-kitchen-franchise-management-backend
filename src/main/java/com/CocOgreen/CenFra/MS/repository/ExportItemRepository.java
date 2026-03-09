package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ExportItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportItemRepository extends JpaRepository<ExportItem, Integer> {
    Page<ExportItem> findAll(Pageable pageable);

}
