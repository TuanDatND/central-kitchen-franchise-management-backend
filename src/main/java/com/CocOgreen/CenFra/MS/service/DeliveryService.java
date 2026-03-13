package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.DeliveryDto;
import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.request.DeliveryRequest;
import com.CocOgreen.CenFra.MS.entity.Delivery;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.DeliveryStatus;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.mapper.DeliveryMapper;
import com.CocOgreen.CenFra.MS.mapper.ExportNoteMapper;
import com.CocOgreen.CenFra.MS.repository.DeliveryRepository;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ExportNoteRepository exportNoteRepository;
    private final DeliveryMapper deliveryMapper;
    private final UserRepository userRepository;
    private final ExportNoteMapper exportNoteMapper;

    public List<ExportNoteDto> getReadyExportNote(){
        return exportNoteRepository.findByStatus(ExportStatus.READY).stream().map(exportNoteMapper::toDto).collect(Collectors.toList());
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