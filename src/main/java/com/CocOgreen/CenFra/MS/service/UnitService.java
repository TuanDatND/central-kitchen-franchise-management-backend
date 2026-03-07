package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.request.UnitRequest;
import com.CocOgreen.CenFra.MS.dto.response.UnitResponse;
import com.CocOgreen.CenFra.MS.entity.Unit;
import com.CocOgreen.CenFra.MS.enums.UnitStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.mapper.UnitMapper;
import com.CocOgreen.CenFra.MS.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    /**
     * Lấy toàn bộ danh sách đơn vị
     */
    @Transactional(readOnly = true)
    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAll().stream()
                .map(unitMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy đơn vị theo ID
     */
    @Transactional(readOnly = true)
    public UnitResponse getUnitById(Integer id) {
        Unit unit = findUnitByIdOrThrow(id);
        return unitMapper.toResponse(unit);
    }

    /**
     * Tạo mới đơn vị
     */
    @Transactional
    public UnitResponse createUnit(UnitRequest request) {
        if (unitRepository.existsByUnitNameIgnoreCase(request.getUnitName())) {
            throw new IllegalArgumentException("Đơn vị tên '" + request.getUnitName() + "' đã tồn tại.");
        }

        Unit unit = unitMapper.toEntity(request);
        // Status mặc định là ACTIVE như entity

        Unit savedUnit = unitRepository.save(unit);
        return unitMapper.toResponse(savedUnit);
    }

    /**
     * Cập nhật thông tin đơn vị
     */
    @Transactional
    public UnitResponse updateUnit(Integer id, UnitRequest request) {
        Unit unit = findUnitByIdOrThrow(id);

        if (!unit.getUnitName().equalsIgnoreCase(request.getUnitName()) &&
                unitRepository.existsByUnitNameIgnoreCase(request.getUnitName())) {
            throw new IllegalArgumentException("Đơn vị tên '" + request.getUnitName() + "' đã tồn tại.");
        }

        unitMapper.updateEntityFromRequest(request, unit);

        Unit updatedUnit = unitRepository.save(unit);
        return unitMapper.toResponse(updatedUnit);
    }

    /**
     * Xóa mềm đơn vị (chuyển status thành INACTIVE)
     */
    @Transactional
    public void deleteUnit(Integer id) {
        Unit unit = findUnitByIdOrThrow(id);
        unit.setStatus(UnitStatus.INACTIVE);
        unitRepository.save(unit);
    }

    /**
     * Hàm dùng chung để tìm kiếm theo ID hoặc ném lỗi
     */
    private Unit findUnitByIdOrThrow(Integer id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị với ID: " + id));
    }
}
