package mohamed.boufous.scannerservice.mapper;

import mohamed.boufous.scannerservice.dto.response.ScanResultResponse;
import mohamed.boufous.scannerservice.entity.ScanResult;

public final class ScanResultMapper {

    private ScanResultMapper() {}

    public static ScanResultResponse toResponse(ScanResult entity) {
        return new ScanResultResponse(
                entity.getId(),
                entity.getScanner(),
                entity.getTarget(),
                entity.getSeverity(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getRecommendation(),
                entity.getEvidence(),
                entity.getCwe(),
                entity.getCvss(),
                entity.getCreatedAt()
        );
    }
}
