package mohamed.boufous.scannerservice.controller;

import jakarta.validation.Valid;
import mohamed.boufous.scannerservice.constants.ApiConstants;
import mohamed.boufous.scannerservice.dto.request.ScanTargetCreateRequest;
import mohamed.boufous.scannerservice.dto.request.ScanTargetUpdateRequest;
import mohamed.boufous.scannerservice.dto.response.ScanTargetResponse;
import mohamed.boufous.scannerservice.enums.ScanStatus;
import mohamed.boufous.scannerservice.service.ScanTargetService;
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
@RequestMapping(ApiConstants.BASE_PATH)
public class ScanTargetController {
 

    private final ScanTargetService scanTargetService;

    public ScanTargetController(ScanTargetService scanTargetService) {
        this.scanTargetService = scanTargetService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<ScanTargetResponse>> getAllTargets(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(scanTargetService.findAll(pageable));
    }

    @GetMapping(ApiConstants.ID_PATH)
    public ResponseEntity<ScanTargetResponse> getTargetById(@PathVariable UUID id) {
        return ResponseEntity.ok(scanTargetService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ScanTargetResponse> createTarget(@Valid @RequestBody ScanTargetCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scanTargetService.create(request));
    }

    @PutMapping(ApiConstants.ID_PATH)
    public ResponseEntity<ScanTargetResponse> updateTarget(
            @PathVariable UUID id,
            @Valid @RequestBody ScanTargetUpdateRequest request
    ) {
        return ResponseEntity.ok(scanTargetService.update(id, request));
    }

    @DeleteMapping(ApiConstants.ID_PATH)
    public ResponseEntity<Void> deleteTarget(@PathVariable UUID id) {
        scanTargetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiConstants.STATUS_PATH)
    public ResponseEntity<List<ScanTargetResponse>> getTargetsByStatus(@PathVariable String status) {
        ScanStatus scanStatus = ScanStatus.fromString(status);
        return ResponseEntity.ok(scanTargetService.findByStatus(scanStatus));
    }
}
