package mohamed.boufous.scannerservice.service;

import mohamed.boufous.scannerservice.dto.request.ScanTargetCreateRequest;
import mohamed.boufous.scannerservice.dto.request.ScanTargetUpdateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanTargetResponse;
import mohamed.boufous.scannerservice.enums.ScanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ScanTargetService {
    Page<ScanTargetResponse> findAll(Pageable pageable);
    ScanTargetResponse findById(UUID id);
    ScanTargetResponse create(ScanTargetCreateRequest request);
    ScanTargetResponse update(UUID id, ScanTargetUpdateRequest request);
    void delete(UUID id);
    List<ScanTargetResponse> findByStatus(ScanStatus status);
}
