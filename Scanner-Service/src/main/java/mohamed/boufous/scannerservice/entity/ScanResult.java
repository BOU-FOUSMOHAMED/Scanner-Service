package mohamed.boufous.scannerservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String scanner;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String evidence;

    private String cwe;

    private Double cvss;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ScanResult() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ScanResult(String scanner, String target, String severity, String title,
                      String description, String recommendation, String evidence,
                      String cwe, Double cvss) {
        this.scanner = scanner;
        this.target = target;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.recommendation = recommendation;
        this.evidence = evidence;
        this.cwe = cwe;
        this.cvss = cvss;
    }

    public UUID getId() {
        return id;
    }

    

    public String getScanner() {
        return scanner;
    }

    public void setScanner(String scanner) {
        this.scanner = scanner;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getCwe() {
        return cwe;
    }

    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    public Double getCvss() {
        return cvss;
    }

    public void setCvss(Double cvss) {
        this.cvss = cvss;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
