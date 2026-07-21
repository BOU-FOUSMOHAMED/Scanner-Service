package mohamed.boufous.scannerservice.service.impl;

import mohamed.boufous.scannerservice.constants.MessageConstants;
import mohamed.boufous.scannerservice.dto.request.ScanTargetCreateRequest;
import mohamed.boufous.scannerservice.dto.request.ScanTargetUpdateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanTargetResponse;
import mohamed.boufous.scannerservice.entity.ScanTarget;
import mohamed.boufous.scannerservice.enums.ScanStatus;
import mohamed.boufous.scannerservice.exception.BusinessException;
import mohamed.boufous.scannerservice.exception.ResourceNotFoundException;
import mohamed.boufous.scannerservice.mapper.ScanTargetMapper;
import mohamed.boufous.scannerservice.repository.ScanTargetRepository;
import mohamed.boufous.scannerservice.service.ScanTargetService;
import mohamed.boufous.scannerservice.util.UrlValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScanTargetServiceImpl implements ScanTargetService {

    private final ScanTargetRepository repository;

    public ScanTargetServiceImpl(ScanTargetRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScanTargetResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ScanTargetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ScanTargetResponse findById(UUID id) {
        ScanTarget entity = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forScanTarget(id));
        return ScanTargetMapper.toResponse(entity);
    }

    @Override
    public ScanTargetResponse create(ScanTargetCreateRequest request) {
        String normalizedUrl = UrlValidator.normalize(request.url());
        if (!UrlValidator.isValid(normalizedUrl)) {
            throw new BusinessException(MessageConstants.INVALID_URL);
        }
        if (repository.existsByUrl(normalizedUrl)) {
            throw new BusinessException(MessageConstants.DUPLICATE_URL);
        }
        ScanTarget entity = new ScanTarget(normalizedUrl);
        entity.setStatus(ScanStatus.ARRIVED);
        try {
            ScanTarget saved = repository.save(entity);
            return ScanTargetMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(MessageConstants.DUPLICATE_URL);
        }
    }

    @Override
    public ScanTargetResponse update(UUID id, ScanTargetUpdateRequest request) {
        ScanTarget existing = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forScanTarget(id));
        String normalizedUrl = UrlValidator.normalize(request.url());
        if (!UrlValidator.isValid(normalizedUrl)) {
            throw new BusinessException(MessageConstants.INVALID_URL);
        }
        if (repository.existsByUrlAndIdNot(normalizedUrl, id)) {
            throw new BusinessException(MessageConstants.DUPLICATE_URL);
        }
        existing.setUrl(normalizedUrl);
        ScanTarget saved = repository.save(existing);
        return ScanTargetMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        ScanTarget entity = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forScanTarget(id));
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanTargetResponse> findByStatus(ScanStatus status) {
        return repository.findByStatus(status).stream()
                .map(ScanTargetMapper::toResponse)
                .toList();
    }
}
