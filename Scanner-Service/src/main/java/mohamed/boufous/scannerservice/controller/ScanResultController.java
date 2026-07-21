package mohamed.boufous.scannerservice.controller;

import jakarta.validation.Valid;
import mohamed.boufous.scannerservice.constants.ApiConstants;
import mohamed.boufous.scannerservice.dto.request.ScanResultCreateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanResultResponse;
import mohamed.boufous.scannerservice.service.ScanResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.BASE_PATH + ApiConstants.RESULTS_PATH)
public class ScanResultController {

    private final ScanResultService scanResultService;

    public ScanResultController(ScanResultService scanResultService) {
        this.scanResultService = scanResultService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<ScanResultResponse>> getAllResults(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(scanResultService.findAll(pageable));
    }

    @GetMapping(ApiConstants.ID_PATH)
    public ResponseEntity<ScanResultResponse> getResultById(@PathVariable UUID id) {
        return ResponseEntity.ok(scanResultService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ScanResultResponse> createResult(@Valid @RequestBody ScanResultCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scanResultService.create(request));
    }

    @GetMapping(ApiConstants.TARGET_PATH)
    public ResponseEntity<List<ScanResultResponse>> getResultsByTarget(@PathVariable String target) {
        return ResponseEntity.ok(scanResultService.findByTarget(target));
    }

    @GetMapping(ApiConstants.SCANNER_PATH)
    public ResponseEntity<List<ScanResultResponse>> getResultsByScanner(@PathVariable String scanner) {
        return ResponseEntity.ok(scanResultService.findByScanner(scanner));
    }

    @GetMapping(ApiConstants.SEVERITY_PATH)
    public ResponseEntity<List<ScanResultResponse>> getResultsBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(scanResultService.findBySeverity(severity));
    }
}
