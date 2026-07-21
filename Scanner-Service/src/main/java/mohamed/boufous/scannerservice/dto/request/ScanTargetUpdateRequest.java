package mohamed.boufous.scannerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import mohamed.boufous.scannerservice.constants.MessageConstants;

public record ScanTargetUpdateRequest(
        @NotBlank(message = MessageConstants.URL_REQUIRED)
        String url
) {}
