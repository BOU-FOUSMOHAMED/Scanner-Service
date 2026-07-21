package mohamed.boufous.scannerservice.service.impl;

import mohamed.boufous.scannerservice.dto.request.ScanResultCreateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanResultResponse;
import mohamed.boufous.scannerservice.entity.ScanResult;
import mohamed.boufous.scannerservice.exception.ResourceNotFoundException;
import mohamed.boufous.scannerservice.mapper.ScanResultMapper;
import mohamed.boufous.scannerservice.repository.ScanResultRepository;
import mohamed.boufous.scannerservice.service.ScanResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScanResultServiceImpl implements ScanResultService {

    private final ScanResultRepository repository;

    public ScanResultServiceImpl(ScanResultRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScanResultResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ScanResultMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ScanResultResponse findById(UUID id) {
        ScanResult entity = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forScanResult(id));
        return ScanResultMapper.toResponse(entity);
    }

    @Override
    public ScanResultResponse create(ScanResultCreateRequest request) {
        ScanResult entity = new ScanResult(
                request.scanner(),
                request.target(),
                request.severity(),
                request.title(),
                request.description(),
                request.recommendation(),
                request.evidence(),
                request.cwe(),
                request.cvss()
        );
        ScanResult saved = repository.save(entity);
        return ScanResultMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanResultResponse> findByTarget(String target) {
        return repository.findByTarget(target).stream()
                .map(ScanResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanResultResponse> findByScanner(String scanner) {
        return repository.findByScanner(scanner).stream()
                .map(ScanResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanResultResponse> findBySeverity(String severity) {
        return repository.findBySeverity(severity).stream()
                .map(ScanResultMapper::toResponse)
                .toList();
    }
}
