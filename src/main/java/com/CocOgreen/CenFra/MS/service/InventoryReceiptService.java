package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.request.InventoryReceiptRequest;
import com.CocOgreen.CenFra.MS.dto.response.InventoryReceiptResponse;
import com.CocOgreen.CenFra.MS.entity.InventoryReceipt;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.entity.ReceiptItem;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.mapper.InventoryReceiptMapper;
import com.CocOgreen.CenFra.MS.repository.InventoryReceiptRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import com.CocOgreen.CenFra.MS.repository.ReceiptItemRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;

/**
 * Service xử lý Nhập Kho (Inbound Inventory Receipt)
 * Thuộc phạm vi Backend Dev 2 (Master Data & Inbound)
 */
@Service
@RequiredArgsConstructor
public class InventoryReceiptService {

    private final InventoryReceiptRepository inventoryReceiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductBatchRepository productBatchRepository;
    private final UserRepository userRepository;
    private final InventoryReceiptMapper inventoryReceiptMapper;

    /**
     * Tạo mới phiếu nhập kho dựa trên số lượng thực tế kiểm kê được từ Bếp.
     * 
     * @param request Data transfer object chứa danh sách lô hàng và số lượng thực
     *                tế.
     * @return Dữ liệu phiếu nhập kho sau khi đã tạo thành công
     */
    @Transactional
    public InventoryReceiptResponse createReceipt(InventoryReceiptRequest request) {
        // Lấy User đăng nhập hiện hành
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUserName(currentUserName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng hiện tại trong hệ thống: " + currentUserName));

        // 1. Dùng mapper để sinh Entity rỗng từ Request
        InventoryReceipt receipt = inventoryReceiptMapper.toEntity(request);

        // Cập nhật thông tin phiếu (Sinh mã PN theo cú pháp IR-Thời gian thực)
        receipt.setReceiptCode("IR-" + System.currentTimeMillis());
        receipt.setCreatedBy(currentUser);

        // Lưu trước vỏ Phiếu Nhập để có ID dùng cho các chi tiết Items
        InventoryReceipt savedReceipt = inventoryReceiptRepository.save(receipt);

        List<ReceiptItem> receiptItemsToSave = new ArrayList<>();
        List<ProductBatch> batchesToUpdate = new ArrayList<>();

        // 2. Loop qua từng item (các lô hàng được bàn giao)
        for (InventoryReceiptRequest.ReceiptItemRequest itemRequest : request.getItems()) {

            // Tìm lô hàng bằng Batch ID
            ProductBatch batch = productBatchRepository.findById(itemRequest.getProductBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy Lô hàng (ProductBatch) với ID: " + itemRequest.getProductBatchId()));

            // Kiểm tra trạng thái hợp lệ. Chỉ nhập lô đang ĐỢI HÀNG.
            if (batch.getStatus() != BatchStatus.WAITING_FOR_STOCK) {
                throw new IllegalArgumentException(
                        "Lô hàng " + batch.getBatchCode() + " đã được xử lý hoặc đã hết hạn. Trạng thái hiện tại: "
                                + batch.getStatus());
            }

            // Mở khóa Lô hàng: Cập nhật status và số lượng xuất kho thực tế
            batch.setCurrentQuantity(itemRequest.getQuantity());
            batch.setStatus(BatchStatus.AVAILABLE);
            batchesToUpdate.add(batch);

            // Sinh chi tiết phiếu nhập (Receipt Item) kết nối 2 bảng
            ReceiptItem receiptItem = new ReceiptItem();
            receiptItem.setInventoryReceipt(savedReceipt);
            receiptItem.setProductBatch(batch);
            receiptItem.setQuantity(itemRequest.getQuantity());
            receiptItemsToSave.add(receiptItem);
        }

        // 3. Batch lưu vào Cơ sở dữ liệu
        productBatchRepository.saveAll(batchesToUpdate);
        List<ReceiptItem> savedItems = receiptItemRepository.saveAll(receiptItemsToSave);

        // Nạp lại danh sách Items vào đối tượng Phiếu để phục vụ Mapping response
        savedReceipt.setReceiptItems(savedItems);

        // 4. Map sang Response và trả kết quả
        return inventoryReceiptMapper.toResponse(savedReceipt);
    }

    /**
     * Lấy danh sách tất cả phiếu nhập kho, sắp xếp theo thời gian mới nhất (DESC).
     */
    @Transactional(readOnly = true)
    public List<InventoryReceiptResponse> getAllReceipts() {
        return inventoryReceiptRepository.findAll(Sort.by(Sort.Direction.DESC, "receiptDate"))
                .stream()
                .map(inventoryReceiptMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết của một phiếu nhập kho theo ID.
     */
    @Transactional(readOnly = true)
    public InventoryReceiptResponse getReceiptById(Integer id) {
        InventoryReceipt receipt = inventoryReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory receipt not found"));
        return inventoryReceiptMapper.toResponse(receipt);
    }
}
