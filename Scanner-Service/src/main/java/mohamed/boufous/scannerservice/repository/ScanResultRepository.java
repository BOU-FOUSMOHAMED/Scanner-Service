package mohamed.boufous.scannerservice.repository;

import mohamed.boufous.scannerservice.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScanResultRepository extends JpaRepository<ScanResult, UUID> {
    List<ScanResult> findByTarget(String target);
    List<ScanResult> findByScanner(String scanner);
    List<ScanResult> findBySeverity(String severity);
}
