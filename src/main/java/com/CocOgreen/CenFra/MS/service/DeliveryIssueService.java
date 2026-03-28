package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.DeliveryIssueItemRequest;
import com.CocOgreen.CenFra.MS.dto.DeliveryIssueItemResponse;
import com.CocOgreen.CenFra.MS.dto.DeliveryIssueResponse;
import com.CocOgreen.CenFra.MS.dto.OrderActionActorDTO;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.RejectDeliveryRequest;
import com.CocOgreen.CenFra.MS.dto.ReviewDeliveryIssueRequest;
import com.CocOgreen.CenFra.MS.dto.UploadedFileResult;
import com.CocOgreen.CenFra.MS.entity.DeliveryIssue;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueDecision;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueReason;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueResolution;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.repository.DeliveryIssueRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryIssueService {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String DELIVERY_ISSUE_IMAGE_FOLDER = "delivery-issues";

    private final DeliveryIssueRepository deliveryIssueRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    @Transactional
    public DeliveryIssueResponse reportIssue(Integer orderId, RejectDeliveryRequest request, List<MultipartFile> images) {
        Authentication auth = getAuthentication();
        requireAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF);

        StoreOrder order = findOrder(orderId);
        validateStoreStaffOwnership(order, auth.getName());
        validateReportableState(order, request.getReason());

        if (deliveryIssueRepository.existsByStoreOrder_OrderIdAndStatus(orderId, DeliveryIssueStatus.PENDING_REVIEW)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng này đã có một issue đang chờ coordinator xử lý");
        }
        if (deliveryIssueRepository.existsByStoreOrder_OrderIdAndStatus(orderId, DeliveryIssueStatus.APPROVED)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng này đã được xử lý issue trước đó. Vui lòng theo dõi đơn giao bù hoặc đơn thay thế");
        }

        List<DeliveryIssueItemRequest> requestItems = normalizeRequestItems(request.getItems());
        validateIssuePayload(order, request.getReason(), requestItems, images);

        List<DeliveryIssueItemRequest> normalizedItems = normalizeIssueItems(order, request.getReason(), requestItems);
        if (requiresIssueItems(request.getReason()) && normalizedItems.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue này cần ít nhất 1 sản phẩm thực sự bị ảnh hưởng");
        }

        int totalQuantity = calculateTotalQuantity(order);
        int affectedQuantity = calculateAffectedQuantity(normalizedItems);
        DeliveryIssueResolution recommendedResolution = resolveRecommendedResolution(
                request.getReason(),
                affectedQuantity,
                totalQuantity);

        User reporter = getCurrentUser(auth.getName());
        StoreOrderStatus reportedOrderStatus = order.getStatus();
        order.markDeliveryIssuePending();

        DeliveryIssue issue = new DeliveryIssue();
        issue.setStoreOrder(order);
        issue.setReportedOrderStatus(reportedOrderStatus);
        issue.setReason(request.getReason());
        issue.setNote(request.getNote());
        issue.setTotalQuantity(totalQuantity);
        issue.setAffectedQuantity(affectedQuantity == 0 ? null : affectedQuantity);
        issue.setIssueItemsJson(writeIssueItems(normalizedItems));
        issue.setRecommendedResolution(recommendedResolution);
        issue.setReportedBy(reporter);
        issue.setReportedAt(LocalDateTime.now());
        issue.setStatus(DeliveryIssueStatus.PENDING_REVIEW);
        issue.setImageUrls(writeUploadedImages(images));

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
            validateOrderStateForIssueApproval(issue, originalOrder);

            DeliveryIssueResolution selectedResolution = request.getResolution() == null
                    ? issue.getRecommendedResolution()
                    : request.getResolution();
            validateSelectedResolution(issue, selectedResolution);
            issue.setSelectedResolution(selectedResolution);

            updateOriginalOrderStatusAfterApproval(originalOrder, selectedResolution);

            StoreOrder replacementOrder = createReplacementOrder(originalOrder, issue, selectedResolution,
                    request.getNewDeliveryDate());
            if (replacementOrder != null) {
                issue.setReplacementOrder(storeOrderRepository.save(replacementOrder));
            }
            issue.setStatus(DeliveryIssueStatus.APPROVED);
        } else {
            if (request.getNewDeliveryDate() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Reject issue không cho phép truyền ngày giao mới");
            }
            if (request.getResolution() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Reject issue không cho phép truyền hướng xử lý");
            }
            originalOrder.setStatus(issue.getReportedOrderStatus());
            issue.setStatus(DeliveryIssueStatus.REJECTED);
        }

        issue.setReviewDecision(request.getDecision());
        issue.setReviewedBy(reviewer);
        issue.setReviewedAt(LocalDateTime.now());

        return toResponse(issue);
    }

    private void validateReportableState(StoreOrder order, DeliveryIssueReason reason) {
        if (reason == DeliveryIssueReason.QUALITY_FAILED) {
            if (order.getStatus() != StoreOrderStatus.DONE) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "QUALITY_FAILED chỉ được báo khi đơn đã ở trạng thái DONE");
            }
            return;
        }

        if (supportsReceivedOrInTransit(reason)) {
            if (order.getStatus() != StoreOrderStatus.DONE && order.getStatus() != StoreOrderStatus.IN_TRANSIT) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Loại issue này chỉ được báo khi đơn đang ở trạng thái IN_TRANSIT hoặc DONE");
            }
            if (reason == DeliveryIssueReason.DAMAGED && order.getStatus() == StoreOrderStatus.DONE) {
                validateWithin24Hours(order);
            }
            return;
        }

        if (order.getStatus() != StoreOrderStatus.IN_TRANSIT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loại issue này chỉ được báo khi đơn đang ở trạng thái IN_TRANSIT");
        }
    }

    private void validateIssuePayload(
            StoreOrder order,
            DeliveryIssueReason reason,
            List<DeliveryIssueItemRequest> items,
            List<MultipartFile> images) {
        if (requiresEvidence(reason) && !hasAnyValidImage(images)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue này bắt buộc phải có ảnh chứng minh");
        }

        if (requiresIssueItems(reason) && items.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue này bắt buộc phải khai báo danh sách sản phẩm bị ảnh hưởng");
        }

        validateIssueItemsAgainstOrder(order, reason, items);

        List<DeliveryIssueItemRequest> normalizedItems = normalizeIssueItems(order, reason, items);
        int totalQuantity = calculateTotalQuantity(order);
        int affectedQuantity = calculateAffectedQuantity(normalizedItems);

        if (!normalizedItems.isEmpty() && affectedQuantity > totalQuantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tổng số lượng bị ảnh hưởng không được vượt quá tổng số lượng của đơn");
        }
    }

    private void validateIssueItemsAgainstOrder(
            StoreOrder order,
            DeliveryIssueReason reason,
            List<DeliveryIssueItemRequest> items) {
        if (items.isEmpty()) {
            return;
        }

        long distinctProductCount = items.stream()
                .map(DeliveryIssueItemRequest::getProductId)
                .distinct()
                .count();
        if (distinctProductCount != items.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Danh sách sản phẩm bị ảnh hưởng không được chứa productId trùng nhau");
        }

        Map<Integer, Integer> orderedQuantities = order.getOrderDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getProductId(),
                        OrderDetail::getQuantity,
                        Integer::sum));

        for (DeliveryIssueItemRequest item : items) {
            Integer orderedQuantity = orderedQuantities.get(item.getProductId());
            if (orderedQuantity == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + item.getProductId() + " không tồn tại trong đơn hàng này");
            }

            if (usesWholeLineQuantity(reason)) {
                continue;
            }

            if (usesReceivedQuantity(reason) && item.getReceivedQuantity() != null) {
                if (item.getReceivedQuantity() > orderedQuantity) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Số lượng nhận của sản phẩm " + item.getProductId()
                                    + " không được vượt quá số lượng đã đặt");
                }
                continue;
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                String message = usesReceivedQuantity(reason)
                        ? "Sản phẩm " + item.getProductId()
                        + " phải có quantity hoặc receivedQuantity hợp lệ"
                        : "Sản phẩm " + item.getProductId() + " phải có quantity hợp lệ";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }

            if (item.getQuantity() > orderedQuantity) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Số lượng bị ảnh hưởng của sản phẩm " + item.getProductId()
                                + " không được vượt quá số lượng đã đặt");
            }
        }
    }

    private void validateWithin24Hours(StoreOrder order) {
        if (order.getReceivedAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng chưa có thời điểm nhận hàng để kiểm tra điều kiện 24h");
        }
        long hours = ChronoUnit.HOURS.between(order.getReceivedAt(), LocalDateTime.now());
        if (hours > 24) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue này chỉ được báo trong vòng 24h sau khi nhận hàng");
        }
    }

    private void validateOrderStateForIssueApproval(DeliveryIssue issue, StoreOrder order) {
        StoreOrderStatus reportedStatus = issue.getReportedOrderStatus();
        if (reportedStatus == StoreOrderStatus.IN_TRANSIT
                && order.getStatus() != StoreOrderStatus.IN_TRANSIT
                && order.getStatus() != StoreOrderStatus.DELIVERY_ISSUE_PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể approve issue khi đơn đang ở trạng thái IN_TRANSIT hoặc DELIVERY_ISSUE_PENDING");
        }
        if (reportedStatus == StoreOrderStatus.DONE
                && order.getStatus() != StoreOrderStatus.DONE
                && order.getStatus() != StoreOrderStatus.DELIVERY_ISSUE_PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể approve issue sau nhận hàng khi đơn đang ở trạng thái DONE hoặc DELIVERY_ISSUE_PENDING");
        }
    }

    private void validateSelectedResolution(DeliveryIssue issue, DeliveryIssueResolution resolution) {
        if (resolution == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approve issue phải có hướng xử lý");
        }

        List<DeliveryIssueResolution> allowed = switch (issue.getReason()) {
            case DAMAGED -> List.of(DeliveryIssueResolution.REPLACE_FULL, DeliveryIssueResolution.REPLACE_PARTIAL);
            case MISSING_ITEMS -> List.of(DeliveryIssueResolution.BACKORDER);
            case WRONG_ITEMS -> List.of(DeliveryIssueResolution.REDELIVER_CORRECT_ITEMS);
            case QUALITY_FAILED -> List.of(DeliveryIssueResolution.REPLACE_FULL);
            case LATE_DELIVERY, REFUSED_DELIVERY -> List.of(DeliveryIssueResolution.REJECT_DELIVERY);
        };

        if (!allowed.contains(resolution)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hướng xử lý không hợp lệ với loại issue hiện tại");
        }
    }

    private void updateOriginalOrderStatusAfterApproval(
            StoreOrder originalOrder,
            DeliveryIssueResolution resolution) {
        switch (resolution) {
            case REPLACE_FULL, REJECT_DELIVERY -> originalOrder.markDeliveryFailed();
            case REPLACE_PARTIAL, BACKORDER, REDELIVER_CORRECT_ITEMS -> originalOrder.setStatus(StoreOrderStatus.DONE);
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không hỗ trợ cập nhật trạng thái đơn cũ với hướng xử lý hiện tại");
        }
    }

    private StoreOrder createReplacementOrder(
            StoreOrder originalOrder,
            DeliveryIssue issue,
            DeliveryIssueResolution resolution,
            LocalDate requestedDeliveryDate) {
        if (resolution == DeliveryIssueResolution.REJECT_DELIVERY
                || resolution == DeliveryIssueResolution.DESTROY_AT_STORE) {
            return null;
        }

        LocalDate replacementDeliveryDate = requestedDeliveryDate == null
                ? originalOrder.getDeliveryDate()
                : requestedDeliveryDate;
        validateDeliveryDate(replacementDeliveryDate);

        StoreOrder replacement = new StoreOrder(
                generateReplacementOrderCode(originalOrder.getOrderCode()),
                originalOrder.getStore(),
                replacementDeliveryDate);
        replacement.setStatus(StoreOrderStatus.APPROVED);

        if (resolution == DeliveryIssueResolution.REPLACE_FULL) {
            for (OrderDetail originalDetail : originalOrder.getOrderDetails()) {
                OrderDetail replacementDetail = new OrderDetail();
                replacementDetail.setProduct(originalDetail.getProduct());
                replacementDetail.setQuantity(originalDetail.getQuantity());
                replacementDetail.setUnitPrice(originalDetail.getUnitPrice());
                replacement.addOrderDetail(replacementDetail);
            }
            return replacement;
        }

        Map<Integer, OrderDetail> detailByProductId = originalOrder.getOrderDetails().stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getProductId(), Function.identity()));

        for (DeliveryIssueItemRequest item : readIssueItems(issue)) {
            OrderDetail originalDetail = detailByProductId.get(item.getProductId());
            if (originalDetail == null) {
                continue;
            }
            OrderDetail replacementDetail = new OrderDetail();
            replacementDetail.setProduct(originalDetail.getProduct());
            replacementDetail.setQuantity(item.getQuantity());
            replacementDetail.setUnitPrice(originalDetail.getUnitPrice());
            replacement.addOrderDetail(replacementDetail);
        }

        if (replacement.getOrderDetails().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue này chưa có item bị ảnh hưởng để tạo đơn thay thế");
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

    private DeliveryIssueResolution resolveRecommendedResolution(
            DeliveryIssueReason reason,
            int affectedQuantity,
            int totalQuantity) {
        return switch (reason) {
            case DAMAGED -> totalQuantity > 0 && ((double) affectedQuantity / totalQuantity) >= 0.5
                    ? DeliveryIssueResolution.REPLACE_FULL
                    : DeliveryIssueResolution.REPLACE_PARTIAL;
            case MISSING_ITEMS -> DeliveryIssueResolution.BACKORDER;
            case WRONG_ITEMS -> DeliveryIssueResolution.REDELIVER_CORRECT_ITEMS;
            case QUALITY_FAILED -> DeliveryIssueResolution.REPLACE_FULL;
            case LATE_DELIVERY, REFUSED_DELIVERY -> DeliveryIssueResolution.REJECT_DELIVERY;
        };
    }

    private boolean supportsReceivedOrInTransit(DeliveryIssueReason reason) {
        return reason == DeliveryIssueReason.DAMAGED
                || reason == DeliveryIssueReason.MISSING_ITEMS
                || reason == DeliveryIssueReason.WRONG_ITEMS;
    }

    private boolean requiresIssueItems(DeliveryIssueReason reason) {
        return reason == DeliveryIssueReason.DAMAGED
                || reason == DeliveryIssueReason.MISSING_ITEMS
                || reason == DeliveryIssueReason.WRONG_ITEMS
                || reason == DeliveryIssueReason.QUALITY_FAILED;
    }

    private boolean requiresEvidence(DeliveryIssueReason reason) {
        return reason == DeliveryIssueReason.DAMAGED;
    }

    private boolean usesReceivedQuantity(DeliveryIssueReason reason) {
        return reason == DeliveryIssueReason.DAMAGED
                || reason == DeliveryIssueReason.MISSING_ITEMS
                || reason == DeliveryIssueReason.QUALITY_FAILED;
    }

    private boolean usesWholeLineQuantity(DeliveryIssueReason reason) {
        return reason == DeliveryIssueReason.WRONG_ITEMS;
    }

    private int calculateTotalQuantity(StoreOrder order) {
        return order.getOrderDetails().stream()
                .map(OrderDetail::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(0, Integer::sum);
    }

    private int calculateAffectedQuantity(List<DeliveryIssueItemRequest> items) {
        return items.stream()
                .map(DeliveryIssueItemRequest::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(0, Integer::sum);
    }

    private List<DeliveryIssueItemRequest> normalizeRequestItems(List<DeliveryIssueItemRequest> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .filter(item -> item != null && item.getProductId() != null)
                .toList();
    }

    private List<DeliveryIssueItemRequest> normalizeIssueItems(
            StoreOrder order,
            DeliveryIssueReason reason,
            List<DeliveryIssueItemRequest> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> orderedQuantities = order.getOrderDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getProductId(),
                        OrderDetail::getQuantity,
                        Integer::sum));

        List<DeliveryIssueItemRequest> normalizedItems = new ArrayList<>();
        for (DeliveryIssueItemRequest item : items) {
            Integer orderedQuantity = orderedQuantities.get(item.getProductId());
            if (orderedQuantity == null) {
                continue;
            }

            Integer affectedQuantity;
            if (usesWholeLineQuantity(reason)) {
                affectedQuantity = orderedQuantity;
            } else if (usesReceivedQuantity(reason) && item.getReceivedQuantity() != null) {
                affectedQuantity = orderedQuantity - item.getReceivedQuantity();
            } else {
                affectedQuantity = item.getQuantity();
            }

            if (affectedQuantity != null && affectedQuantity > 0) {
                normalizedItems.add(new DeliveryIssueItemRequest(item.getProductId(), affectedQuantity, null));
            }
        }
        return normalizedItems;
    }

    private List<DeliveryIssueItemRequest> readIssueItems(DeliveryIssue issue) {
        if (issue.getIssueItemsJson() == null || issue.getIssueItemsJson().isBlank()) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(
                    objectMapper.readValue(issue.getIssueItemsJson(), new TypeReference<List<DeliveryIssueItemRequest>>() {}));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Không thể parse issue_items_json của delivery issue", e);
        }
    }

    private String writeIssueItems(List<DeliveryIssueItemRequest> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Không thể lưu issue_items_json của delivery issue", e);
        }
    }

    private List<DeliveryIssueItemResponse> toIssueItemResponses(DeliveryIssue issue, StoreOrder originalOrder) {
        Map<Integer, String> productNames = originalOrder.getOrderDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getProductId(),
                        detail -> detail.getProduct().getProductName(),
                        (left, right) -> left));
        return readIssueItems(issue).stream()
                .map(item -> new DeliveryIssueItemResponse(
                        item.getProductId(),
                        productNames.get(item.getProductId()),
                        item.getQuantity()))
                .toList();
    }

    private List<String> readImageUrls(DeliveryIssue issue) {
        if (issue.getImageUrls() == null || issue.getImageUrls().isBlank()) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(
                    objectMapper.readValue(issue.getImageUrls(), new TypeReference<List<String>>() {}));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Không thể parse image_urls của delivery issue", e);
        }
    }

    private String writeImageUrls(List<String> imageUrls) {
        try {
            return objectMapper.writeValueAsString(imageUrls);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Không thể lưu image_urls của delivery issue", e);
        }
    }

    private String writeUploadedImages(List<MultipartFile> images) {
        List<MultipartFile> validImages = images == null ? List.of() : images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (validImages.isEmpty()) {
            return null;
        }

        List<String> imageUrls = validImages.stream()
                .map(image -> fileUploadService.uploadFileWithMetadata(image, DELIVERY_ISSUE_IMAGE_FOLDER))
                .map(UploadedFileResult::getSecureUrl)
                .toList();

        return writeImageUrls(imageUrls);
    }

    private boolean hasAnyValidImage(List<MultipartFile> images) {
        return images != null && images.stream().anyMatch(file -> file != null && !file.isEmpty());
    }

    private DeliveryIssueResponse toResponse(DeliveryIssue issue) {
        StoreOrder originalOrder = issue.getStoreOrder();
        StoreOrder replacementOrder = issue.getReplacementOrder();
        return new DeliveryIssueResponse(
                issue.getIssueId(),
                issue.getStatus(),
                issue.getReportedOrderStatus(),
                issue.getReason(),
                issue.getNote(),
                issue.getTotalQuantity(),
                issue.getAffectedQuantity(),
                toIssueItemResponses(issue, originalOrder),
                issue.getRecommendedResolution(),
                issue.getSelectedResolution(),
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
                replacementOrder == null ? null : replacementOrder.getOrderCode(),
                readImageUrls(issue));
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
        if (hasAnyRole(auth, roles)) {
            return;
        }
        throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này");
    }

    private boolean hasAnyRole(Authentication auth, RoleName... roles) {
        for (RoleName role : roles) {
            String authority = ROLE_PREFIX + role.name();
            boolean matched = auth.getAuthorities().stream().anyMatch(a -> authority.equals(a.getAuthority()));
            if (matched) {
                return true;
            }
        }
        return false;
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
