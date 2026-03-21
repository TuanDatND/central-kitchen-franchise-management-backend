package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.DeliveryIssueResponse;
import com.CocOgreen.CenFra.MS.dto.OrderActionActorDTO;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.RejectDeliveryRequest;
import com.CocOgreen.CenFra.MS.dto.ReviewDeliveryIssueRequest;
import com.CocOgreen.CenFra.MS.entity.DeliveryIssue;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueDecision;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.repository.DeliveryIssueRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryIssueService {
    private static final String ROLE_PREFIX = "ROLE_";

    private final DeliveryIssueRepository deliveryIssueRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeliveryIssueResponse reportIssue(Integer orderId, RejectDeliveryRequest request) {
        Authentication auth = getAuthentication();
        requireAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF);

        StoreOrder order = findOrder(orderId);
        validateStoreStaffOwnership(order, auth.getName());

        if (order.getStatus() != StoreOrderStatus.IN_TRANSIT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được báo sự cố giao hàng khi đơn đang ở trạng thái IN_TRANSIT");
        }

        if (deliveryIssueRepository.existsByStoreOrder_OrderIdAndStatus(orderId, DeliveryIssueStatus.PENDING_REVIEW)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng này đã có một issue đang chờ coordinator xử lý");
        }

        User reporter = getCurrentUser(auth.getName());
        order.markDeliveryIssuePending();

        DeliveryIssue issue = new DeliveryIssue();
        issue.setStoreOrder(order);
        issue.setReason(request.getReason());
        issue.setNote(request.getNote());
        issue.setReportedBy(reporter);
        issue.setReportedAt(LocalDateTime.now());
        issue.setStatus(DeliveryIssueStatus.PENDING_REVIEW);

        return toResponse(deliveryIssueRepository.save(issue));
    }

    @Transactional(readOnly = true)
    public PagedData<DeliveryIssueResponse> listIssues(DeliveryIssueStatus status, int page, int size) {
        Authentication auth = getAuthentication();
        requireAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "reportedAt"));

        Page<DeliveryIssue> issues = status == null
                ? deliveryIssueRepository.findAll(pageable)
                : deliveryIssueRepository.findByStatus(status, pageable);

        return new PagedData<>(
                issues.getContent().stream().map(this::toResponse).toList(),
                issues.getNumber(),
                issues.getSize(),
                issues.getTotalElements(),
                issues.getTotalPages(),
                issues.isFirst(),
                issues.isLast());
    }

    @Transactional(readOnly = true)
    public DeliveryIssueResponse getIssueDetail(Integer issueId) {
        Authentication auth = getAuthentication();
        requireAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER);
        return toResponse(findIssue(issueId));
    }

    @Transactional
    public DeliveryIssueResponse reviewIssue(Integer issueId, ReviewDeliveryIssueRequest request) {
        Authentication auth = getAuthentication();
        requireAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER);

        DeliveryIssue issue = findIssue(issueId);
        if (issue.getStatus() != DeliveryIssueStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issue này đã được xử lý trước đó");
        }

        User reviewer = getCurrentUser(auth.getName());
        StoreOrder originalOrder = issue.getStoreOrder();

        if (request.getDecision() == DeliveryIssueDecision.CREATE_REPLACEMENT_ORDER) {
            originalOrder.markDeliveryFailed();
            StoreOrder replacementOrder = createReplacementOrder(originalOrder, request.getNewDeliveryDate());
            issue.setReplacementOrder(storeOrderRepository.save(replacementOrder));
            issue.setStatus(DeliveryIssueStatus.APPROVED);
        } else {
            LocalDate rescheduledDate = request.getNewDeliveryDate() == null
                    ? originalOrder.getDeliveryDate()
                    : request.getNewDeliveryDate();
            validateDeliveryDate(rescheduledDate);
            originalOrder.setDeliveryDate(rescheduledDate);
            originalOrder.setStatus(StoreOrderStatus.APPROVED);
            issue.setStatus(DeliveryIssueStatus.REJECTED);
        }

        issue.setReviewDecision(request.getDecision());
        issue.setReviewedBy(reviewer);
        issue.setReviewedAt(LocalDateTime.now());

        return toResponse(issue);
    }

    private StoreOrder createReplacementOrder(StoreOrder originalOrder, LocalDate requestedDeliveryDate) {
        LocalDate replacementDeliveryDate = requestedDeliveryDate == null
                ? originalOrder.getDeliveryDate()
                : requestedDeliveryDate;
        validateDeliveryDate(replacementDeliveryDate);

        StoreOrder replacement = new StoreOrder(
                generateReplacementOrderCode(originalOrder.getOrderCode()),
                originalOrder.getStore(),
                replacementDeliveryDate);
        replacement.setStatus(StoreOrderStatus.APPROVED);

        for (OrderDetail originalDetail : originalOrder.getOrderDetails()) {
            OrderDetail replacementDetail = new OrderDetail();
            replacementDetail.setProduct(originalDetail.getProduct());
            replacementDetail.setQuantity(originalDetail.getQuantity());
            replacementDetail.setUnitPrice(originalDetail.getUnitPrice());
            replacement.addOrderDetail(replacementDetail);
        }

        return replacement;
    }

    private String generateReplacementOrderCode(String currentOrderCode) {
        String baseCode = currentOrderCode.replaceFirst("-R\\d+$", "");
        int attempt = 1;
        String candidate = baseCode + "-R" + attempt;
        while (storeOrderRepository.findByOrderCode(candidate).isPresent()) {
            attempt++;
            candidate = baseCode + "-R" + attempt;
        }
        return candidate;
    }

    private DeliveryIssueResponse toResponse(DeliveryIssue issue) {
        StoreOrder originalOrder = issue.getStoreOrder();
        StoreOrder replacementOrder = issue.getReplacementOrder();
        return new DeliveryIssueResponse(
                issue.getIssueId(),
                issue.getStatus(),
                issue.getReason(),
                issue.getNote(),
                originalOrder.getOrderId(),
                originalOrder.getOrderCode(),
                originalOrder.getStatus(),
                originalOrder.getStore().getStoreId(),
                originalOrder.getStore().getStoreName(),
                originalOrder.getDeliveryDate(),
                toActor(issue.getReportedBy()),
                issue.getReportedAt(),
                toActor(issue.getReviewedBy()),
                issue.getReviewedAt(),
                issue.getReviewDecision(),
                replacementOrder == null ? null : replacementOrder.getOrderId(),
                replacementOrder == null ? null : replacementOrder.getOrderCode());
    }

    private OrderActionActorDTO toActor(User user) {
        if (user == null) {
            return null;
        }
        String fullName = user.getFullName();
        if (fullName == null || fullName.isBlank()) {
            fullName = user.getUserName();
        }
        return new OrderActionActorDTO(user.getUserId(), user.getUserName(), fullName);
    }

    private DeliveryIssue findIssue(Integer issueId) {
        return deliveryIssueRepository.findByIssueId(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy delivery issue"));
    }

    private StoreOrder findOrder(Integer orderId) {
        return storeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void validateStoreStaffOwnership(StoreOrder order, String username) {
        User user = getCurrentUser(username);
        Integer userStoreId = user.getStore() == null ? null : user.getStore().getStoreId();
        Integer orderStoreId = order.getStore() == null ? null : order.getStore().getStoreId();
        if (userStoreId == null || !userStoreId.equals(orderStoreId)) {
            throw new AccessDeniedException("You can only access your store orders");
        }
    }

    private void requireAnyRole(Authentication auth, RoleName... roles) {
        for (RoleName role : roles) {
            String authority = ROLE_PREFIX + role.name();
            boolean matched = auth.getAuthorities().stream().anyMatch(a -> authority.equals(a.getAuthority()));
            if (matched) {
                return;
            }
        }
        throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này");
    }

    private void validateDeliveryDate(LocalDate deliveryDate) {
        if (deliveryDate == null) {
            throw new IllegalArgumentException("Ngày giao không được để trống");
        }
        if (deliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Delivery date must be today or in the future");
        }
    }
}
