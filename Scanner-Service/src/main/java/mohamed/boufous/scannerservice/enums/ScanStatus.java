package mohamed.boufous.scannerservice.enums;

public enum ScanStatus {
    ARRIVED,
    PENDING,
    FINISHED;

    public static ScanStatus fromString(String value) {
        try {
            return ScanStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: '" + value + "'. Valid values: ARRIVED, PENDING, FINISHED (case-insensitive)"
            );
        }
    }
}
