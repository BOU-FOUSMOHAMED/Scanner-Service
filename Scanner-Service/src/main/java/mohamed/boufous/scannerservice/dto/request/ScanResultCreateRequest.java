package mohamed.boufous.scannerservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import mohamed.boufous.scannerservice.constants.MessageConstants;

public record ScanResultCreateRequest(
        @NotBlank(message = MessageConstants.SCANNER_REQUIRED)
        String scanner,

        @NotBlank(message = MessageConstants.TARGET_REQUIRED)
        String target,

        @NotBlank(message = MessageConstants.SEVERITY_REQUIRED)
        @Pattern(regexp = "CRITICAL|HIGH|MEDIUM|LOW|INFO", message = "Severity must be one of: CRITICAL, HIGH, MEDIUM, LOW, INFO")
        String severity,

        @NotBlank(message = MessageConstants.TITLE_REQUIRED)
        String title,

        String description,

        String recommendation,

        String evidence,

        String cwe,

        @NotNull(message = MessageConstants.CVSS_REQUIRED)
        @DecimalMin(value = "0.0", message = "CVSS must be >= 0.0")
        @DecimalMax(value = "10.0", message = "CVSS must be <= 10.0")
        Double cvss
) {}
