package mohamed.boufous.scannerservice.service;

import mohamed.boufous.scannerservice.dto.request.ScanResultCreateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanResultResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ScanResultService {
    Page<ScanResultResponse> findAll(Pageable pageable);
    ScanResultResponse findById(UUID id);
    ScanResultResponse create(ScanResultCreateRequest request);
    List<ScanResultResponse> findByTarget(String target);
    List<ScanResultResponse> findByScanner(String scanner);
    List<ScanResultResponse> findBySeverity(String severity);
}
