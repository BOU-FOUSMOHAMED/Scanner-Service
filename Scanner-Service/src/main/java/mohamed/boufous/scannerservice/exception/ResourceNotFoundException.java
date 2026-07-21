package mohamed.boufous.scannerservice.exception;

import mohamed.boufous.scannerservice.constants.MessageConstants;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forScanTarget(UUID id) {
        return new ResourceNotFoundException(String.format(MessageConstants.SCAN_TARGET_NOT_FOUND, id));
    }

    public static ResourceNotFoundException forScanResult(UUID id) {
        return new ResourceNotFoundException(String.format(MessageConstants.SCAN_RESULT_NOT_FOUND, id));
    }
}
