package mohamed.boufous.scannerservice.mapper;

import mohamed.boufous.scannerservice.dto.response.ScanTargetResponse;
import mohamed.boufous.scannerservice.entity.ScanTarget;

public final class ScanTargetMapper {

    private ScanTargetMapper() {}

    public static ScanTargetResponse toResponse(ScanTarget entity) {
        return new ScanTargetResponse(
                entity.getId(),
                entity.getUrl(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
