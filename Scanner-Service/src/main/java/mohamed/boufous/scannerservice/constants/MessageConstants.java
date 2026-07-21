package mohamed.boufous.scannerservice.constants;

public final class MessageConstants {

    private MessageConstants() {}

    public static final String SCAN_TARGET_NOT_FOUND = "Scan target not found with id: %s";
    public static final String SCAN_RESULT_NOT_FOUND = "Scan result not found with id: %s";
    public static final String INVALID_URL = "The provided URL is not valid";
    public static final String DUPLICATE_URL = "A scan target with this URL already exists";
    public static final String URL_REQUIRED = "URL is required";
    public static final String SCANNER_REQUIRED = "Scanner name is required";
    public static final String TARGET_REQUIRED = "Target is required";
    public static final String SEVERITY_REQUIRED = "Severity is required";
    public static final String TITLE_REQUIRED = "Title is required";
    public static final String CVSS_REQUIRED = "CVSS score is required";
    public static final String VALIDATION_FAILED = "Request validation failed";
    public static final String INTERNAL_ERROR = "An unexpected error occurred";
}
