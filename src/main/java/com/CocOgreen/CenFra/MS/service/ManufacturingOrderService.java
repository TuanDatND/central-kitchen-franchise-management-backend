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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final ManufacturingOrderMapper manufacturingOrderMapper;

    /**
     * Tạo lệnh sản xuất mới.
     * 
     * @param request Dữ liệu đầu vào chứa productId và quantity (số lượng).
     * @return ManuOrderResponse trả về lệnh vừa tạo.
     */
    @Transactional
    public ManuOrderResponse createOrder(ManuOrderRequest request) {
        // Tìm kiếm sản phẩm theo ID
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        // Map DTO sang Entity. Các trường mặc định như PLANNED sẽ được gán trong quá
        // trình map.
        ManufacturingOrder order = manufacturingOrderMapper.toEntity(request);

        // Gán sản phẩm
        order.setProduct(product);

        // Gán số lượng dự kiến nấu (quantityPlanned)
        order.setQuantityPlanned(request.getQuantity());

        // Tự động sinh mã lệnh sản xuất vd: MO-171500123545
        order.setOrderCode("MO-" + System.currentTimeMillis());

        // Mặc định gán trạng thái PLANNED (dù mapper đã bỏ constant nhưng để an toàn,
        // gán thêm tại đây)
        order.setStatus(ManuOrderStatus.PLANNED);

        // Lấy tên đăng nhập hiện tại từ SecurityContext và tìm User
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUserName(currentUserName)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng hiện tại trong hệ thống: " + currentUserName));
        order.setCreatedBy(currentUser);

        // Lưu xuống DB
        ManufacturingOrder savedOrder = manufacturingOrderRepository.save(order);

        // Map ngược Entity ra Response DTO
        return manufacturingOrderMapper.toResponse(savedOrder);
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
     * Cập nhật trạng thái của lệnh sản xuất.
     * 
     * @param id        ID của lệnh sản xuất.
     * @param newStatus Trạng thái mới (ví dụ PLANNED -> COOKING -> COMPLETED).
     * @return ManuOrderResponse sau khi cập nhật.
     */
    @Transactional
    public ManuOrderResponse updateStatus(Integer id, ManuOrderStatus newStatus) {
        // Tìm MO theo ID
        ManufacturingOrder order = manufacturingOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lệnh sản xuất với ID: " + id));

        // Cập nhật trạng thái mới
        order.setStatus(newStatus);

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
