package mohamed.boufous.scannerservice.dto.response;

import mohamed.boufous.scannerservice.enums.ScanStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScanTargetResponse(
        UUID id,
        String url,
        ScanStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
