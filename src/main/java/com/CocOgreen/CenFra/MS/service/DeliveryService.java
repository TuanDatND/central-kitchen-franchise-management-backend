package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.DeliveryDto;
import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.request.DeliveryRequest;
import com.CocOgreen.CenFra.MS.entity.Delivery;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.DeliveryStatus;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.mapper.DeliveryMapper;
import com.CocOgreen.CenFra.MS.mapper.ExportNoteMapper;
import com.CocOgreen.CenFra.MS.repository.DeliveryRepository;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Để ghi log nếu có lỗi trong quá trình tạo snapshot
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ExportNoteRepository exportNoteRepository;
    private final DeliveryMapper deliveryMapper;
    private final UserRepository userRepository;
    private final ExportNoteMapper exportNoteMapper;
    // ObjectMapper của Jackson để chuyển đổi dữ liệu sang định dạng JSON
    private final ObjectMapper objectMapper;

    public List<ExportNoteDto> getReadyExportNote(){
        return exportNoteRepository.findByStatus(ExportStatus.READY).stream().map(exportNoteMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.CocOgreen.CenFra.MS.dto.DeliveryDetailDto getDeliveryById(Integer id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Chuyến giao hàng ID: " + id));
        return deliveryMapper.toDetailDto(delivery);
    }

    @Transactional(readOnly = true)
    public PagedData<DeliveryDto> findAll(Pageable pageable) {
        Page<Delivery> page = deliveryRepository.findAll(pageable);
        List<DeliveryDto> dtoList = page.getContent().stream()
                .map(deliveryMapper::toDto)
                .collect(Collectors.toList());
        return new PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Transactional
    public DeliveryDto createDelivery(DeliveryRequest request) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUserName(currentUserName)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng hiện tại trong hệ thống."));

        Delivery delivery = new Delivery();
        delivery.setDeliveryCode("DEL-" + System.currentTimeMillis());
        delivery.setDriverName(request.getDriverName());
        delivery.setVehiclePlate(request.getVehiclePlate());
        delivery.setScheduledDate(request.getScheduledDate());
        delivery.setCreatedBy(currentUser);
        delivery.setStatus(DeliveryStatus.PLANNED);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        if (request.getExportNoteIds() != null && !request.getExportNoteIds().isEmpty()) {
            List<ExportNote> exportNotes = exportNoteRepository.findAllById(request.getExportNoteIds());

            for (ExportNote exportNote : exportNotes) {

                if (exportNote.getStatus() != ExportStatus.READY) {
                    throw new IllegalStateException("Phiếu xuất " + exportNote.getExportCode() + " không ở trạng thái READY.");
                }
                exportNote.setDelivery(savedDelivery);
                exportNote.setStatus(ExportStatus.PLANNED);
            }
            exportNoteRepository.saveAll(exportNotes);
            savedDelivery.setExportNotes(exportNotes);
        }

        return deliveryMapper.toDto(savedDelivery);
    }

    @Transactional
    public DeliveryDto startDelivery(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Chuyến giao hàng ID: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PLANNED) {
            throw new IllegalStateException("Chỉ vận hành được các chuyến đang ở trạng thái PLANNED.");
        }

        delivery.setStatus(DeliveryStatus.IN_TRANSIT);
        delivery.setActualStartDate(OffsetDateTime.now());

        // Chuyển toàn bộ phiếu xuất sang trạng thái SHIPPING
        if (delivery.getExportNotes() != null) {
            for (ExportNote note : delivery.getExportNotes()) {
                note.setStatus(ExportStatus.SHIPPING);
            }
            exportNoteRepository.saveAll(delivery.getExportNotes());
        }

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryDto cancelDelivery(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Chuyến giao hàng ID: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PLANNED) {
            throw new IllegalStateException("Chỉ có thể hủy những chuyến xe đang ở trạng thái PLANNED (chưa xuất phát).");
        }

        // === Tạo SNAPSHOT JSON toàn bộ thông tin phiếu xuất TRƯỚC KHI hủy ===
        // Logic này phải chạy trước khi thay đổi bất kỳ trạng thái nào
        try {
            // Tạo mảng JSON chứa danh sách phiếu xuất
            ArrayNode snapshotArray = objectMapper.createArrayNode();

            if (delivery.getExportNotes() != null) {
                for (ExportNote note : delivery.getExportNotes()) {
                    // Mỗi phiếu xuất sẽ là một object JSON riêng
                    ObjectNode noteNode = objectMapper.createObjectNode();
                    noteNode.put("exportCode", note.getExportCode());

                    // Thông tin cửa hàng (luôn lưu tên cửa hàng, trường hợp null thì ghi "Đã hủy")
                    if (note.getStoreOrder() != null && note.getStoreOrder().getStore() != null) {
                        noteNode.put("storeName", note.getStoreOrder().getStore().getStoreName());
                        noteNode.put("storeOrderCode", note.getStoreOrder().getOrderCode());
                    } else {
                        // Dữ liệu không khới tạo được, ghi nhãn rõ ràng
                        noteNode.put("storeName", "Đã hủy - Không xác định");
                        noteNode.put("storeOrderCode", "N/A");
                    }

                    // Thông tin các sản phẩm bên trong phiếu xuất
                    ArrayNode itemsArray = objectMapper.createArrayNode();
                    if (note.getItems() != null) {
                        for (ExportItem item : note.getItems()) {
                            ObjectNode itemNode = objectMapper.createObjectNode();

                            // Thông tin lô hàng và sản phẩm
                            if (item.getProductBatch() != null) {
                                itemNode.put("batchCode", item.getProductBatch().getBatchCode());
                                if (item.getProductBatch().getExpiryDate() != null) {
                                    itemNode.put("expiryDate", item.getProductBatch().getExpiryDate().toString());
                                }
                                // Thông tin sản phẩm từ lô hàng
                                if (item.getProductBatch().getProduct() != null) {
                                    itemNode.put("productName", item.getProductBatch().getProduct().getProductName());
                                    // Dùng var để tránh xung đột tên class Unit trong classpath
                                    // getUnit() trả về Entity Unit, phải lấy getUnitName() (String)
                                    var productUnit = item.getProductBatch().getProduct().getUnit();
                                    itemNode.put("unit", productUnit != null ? productUnit.getUnitName() : "N/A");
                                }
                            }
                            itemNode.put("quantity", item.getQuantity());
                            itemsArray.add(itemNode);
                        }
                    }
                    noteNode.set("items", itemsArray);
                    snapshotArray.add(noteNode);
                }
            }

            // Chuyển đổi toàn bộ thông tin thành chuỗi JSON và lưu vĩnh viễn
            delivery.setCancelledNotesSnapshot(objectMapper.writeValueAsString(snapshotArray));

        } catch (Exception e) {
            // Ghi log lỗi nhưng không chặn luồng hủy - snapshot là tính năng phụ,
            // không được phép làm chảy cả nghiep vụ hủy chính
            log.error("Lỗi khi tạo snapshot cho chuyến xe ID {}: {}", deliveryId, e.getMessage());
        }
        // === Kết thúc tạo SNAPSHOT ===

        // Chuyển trạng thái chuyến xe sang CANCELLED
        delivery.setStatus(DeliveryStatus.CANCELLED);

        // Khôi phục các phiếu xuất về trạng thái READY và giải phóng khỏi chuyến xe
        if (delivery.getExportNotes() != null) {
            // Sao chép danh sách để tránh ConcurrentModificationException và đảm bảo session an toàn
            List<ExportNote> notesToUpdate = new java.util.ArrayList<>(delivery.getExportNotes());
            for (ExportNote note : notesToUpdate) {
                note.setStatus(ExportStatus.READY);
                note.setDelivery(null); // Gỡ liên kết từ phía Phiếu Xuất
            }
            // Quan trọng: Gỡ liên kết từ phía Chuyến Xe để Hibernate không cố gắng đồng bộ lại
            delivery.getExportNotes().clear(); 
            exportNoteRepository.saveAll(notesToUpdate);
        }

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryDto completeDelivery(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Chuyến giao hàng ID: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException("Chỉ hoàn tất được các chuyến đang ở trạng thái IN_TRANSIT (đang giao).");
        }

        delivery.setStatus(DeliveryStatus.COMPLETED);
        delivery.setActualEndDate(OffsetDateTime.now());

        // Chuyển toàn bộ phiếu xuất sang trạng thái SHIPPED (Đã giao tới store)
        if (delivery.getExportNotes() != null) {
            for (ExportNote note : delivery.getExportNotes()) {
                note.setStatus(ExportStatus.SHIPPED);
            }
            exportNoteRepository.saveAll(delivery.getExportNotes());
        }

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }
}