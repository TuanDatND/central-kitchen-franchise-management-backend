package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.DeliveryIssue;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryIssueRepository extends JpaRepository<DeliveryIssue, Integer> {

    @EntityGraph(attributePaths = {"storeOrder", "storeOrder.store", "replacementOrder", "replacementOrder.store", "reportedBy", "reviewedBy"})
    Optional<DeliveryIssue> findByIssueId(Integer issueId);

    @EntityGraph(attributePaths = {"storeOrder", "storeOrder.store", "replacementOrder", "replacementOrder.store", "reportedBy", "reviewedBy"})
    Page<DeliveryIssue> findByStatus(DeliveryIssueStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"storeOrder", "storeOrder.store", "replacementOrder", "replacementOrder.store", "reportedBy", "reviewedBy"})
    Page<DeliveryIssue> findAll(Pageable pageable);

    boolean existsByStoreOrder_OrderIdAndStatus(Integer orderId, DeliveryIssueStatus status);
}
