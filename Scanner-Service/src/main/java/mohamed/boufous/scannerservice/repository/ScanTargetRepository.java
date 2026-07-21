package mohamed.boufous.scannerservice.repository;

import mohamed.boufous.scannerservice.entity.ScanTarget;
import mohamed.boufous.scannerservice.enums.ScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScanTargetRepository extends JpaRepository<ScanTarget, UUID> {
    List<ScanTarget> findByStatus(ScanStatus status);
    boolean existsByUrl(String url);
    boolean existsByUrlAndIdNot(String url, UUID id);
}
