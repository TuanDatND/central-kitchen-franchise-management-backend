package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.request.ManuOrderRequest;
import com.CocOgreen.CenFra.MS.dto.response.ManuOrderResponse;
import com.CocOgreen.CenFra.MS.entity.ManufacturingOrder;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.enums.ManuOrderStatus;
import com.CocOgreen.CenFra.MS.mapper.ManufacturingOrderMapper;
import com.CocOgreen.CenFra.MS.repository.ManufacturingOrderRepository;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Lệnh Sản Xuất (Manufacturing Order).
 * Thuộc phạm vi của Backend Dev 2 (Master Data & Inbound).
 */
@Service
@RequiredArgsConstructor
public class ManufacturingOrderService {

    private final ManufacturingOrderRepository manufacturingOrderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductBatchRepository productBatchRepository;
    private final ManufacturingOrderMapper manufacturingOrderMapper;

    /**
     * Tạo danh sách lệnh sản xuất mới (hàng loạt).
     * 
     * @param request Dữ liệu đầu vào chứa danh sách các items (productId và
     *                quantityPlanned).
     * @return List<ManuOrderResponse> danh sách lệnh vừa tạo.
     */
    @Transactional
    public List<ManuOrderResponse> createOrders(ManuOrderRequest request) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUserName(currentUserName)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng hiện tại trong hệ thống: " + currentUserName));

        List<ManufacturingOrder> ordersToSave = new ArrayList<>();

        for (ManuOrderRequest.Product item : request.getProducts()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm với ID: " + item.getProductId()));

            ManufacturingOrder order = new ManufacturingOrder();
            order.setProduct(product);
            order.setQuantityPlanned(item.getQuantityPlanned());
            // Mã lệnh tự sinh để đảm bảo unique (MO-Timestamp-Random)
            order.setOrderCode("MO-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000));
            order.setStatus(ManuOrderStatus.PLANNED);
            order.setStartDate(Instant.now());
            order.setCreatedBy(currentUser);

            ordersToSave.add(order);
        }

        List<ManufacturingOrder> savedOrders = manufacturingOrderRepository.saveAll(ordersToSave);

        return savedOrders.stream()
                .map(manufacturingOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tất cả các lệnh sản xuất.
     * 
     * @return Danh sách ManuOrderResponse.
     */
    @Transactional(readOnly = true)
    public List<ManuOrderResponse> getAllOrders() {
        // Lấy tất cả và map sang danh sách response
        return manufacturingOrderRepository.findAll().stream()
                .map(manufacturingOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái của lệnh sản xuất (Tự động chuyển tiếp).
     * 
     * @param id ID của lệnh sản xuất.
     * @return ManuOrderResponse sau khi cập nhật.
     */
    @Transactional
    public ManuOrderResponse updateStatus(Integer id) {
        // Tìm MO theo ID, ném ResourceNotFoundException nếu không tìm thấy
        ManufacturingOrder order = manufacturingOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lệnh sản xuất với ID: " + id));

        ManuOrderStatus currentStatus = order.getStatus();
        ManuOrderStatus newStatus;

        // Xác định trạng thái tiếp theo
        if (currentStatus == ManuOrderStatus.PLANNED) {
            newStatus = ManuOrderStatus.COOKING;
        } else if (currentStatus == ManuOrderStatus.COOKING) {
            newStatus = ManuOrderStatus.COMPLETED;
        } else {
            throw new IllegalStateException("Lệnh sản xuất đã hoàn thành, không thể cập nhật thêm trạng thái.");
        }

        // Logic sinh tự động Lô hạng (ProductBatch) khi lệnh sang trạng thái COMPLETED
        if (newStatus == ManuOrderStatus.COMPLETED) {
            ProductBatch newBatch = new ProductBatch();
            newBatch.setProduct(order.getProduct());
            newBatch.setManufacturingOrder(order);
            // Mã lô hàng: BATCH-MãLệnh-Timestamp
            newBatch.setBatchCode("BATCH-" + order.getOrderCode() + "-" + System.currentTimeMillis());
            newBatch.setInitialQuantity(order.getQuantityPlanned());
            newBatch.setCurrentQuantity(0); // Sẽ được cập nhật sau khi nhận kho thực tế
            newBatch.setStatus(BatchStatus.WAITING_FOR_STOCK);
            newBatch.setManufacturingDate(LocalDate.now()); // Ngày sản xuất thực tế bằng ngày hiện tại
            newBatch.setExpiryDate(LocalDate.now().plusDays(7)); // Hạn sử dụng mặc định 7 ngày

            productBatchRepository.save(newBatch);
        }

        // Cập nhật trạng thái mới
        order.setStatus(newStatus);

        // Nếu chuyển sang trạng thái COOKING thì gán thời gian bắt đầu
        if (newStatus == ManuOrderStatus.COOKING) {
            order.setStartDate(Instant.now());
        }

        // Nếu chuyển sang trạng thái COMPLETED thì gán thời gian kết thúc
        if (newStatus == ManuOrderStatus.COMPLETED) {
            order.setEndDate(Instant.now());
        }

        // Lưu đối tượng sau khi update
        ManufacturingOrder updatedOrder = manufacturingOrderRepository.save(order);

        // Trả về response
        return manufacturingOrderMapper.toResponse(updatedOrder);
    }
}
