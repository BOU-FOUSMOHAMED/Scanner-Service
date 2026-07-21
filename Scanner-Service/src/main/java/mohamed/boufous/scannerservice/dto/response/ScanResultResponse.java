package mohamed.boufous.scannerservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScanResultResponse(
        UUID id,
        String scanner,
        String target,
        String severity,
        String title,
        String description,
        String recommendation,
        String evidence,
        String cwe,
        Double cvss,
        LocalDateTime createdAt
) {}
