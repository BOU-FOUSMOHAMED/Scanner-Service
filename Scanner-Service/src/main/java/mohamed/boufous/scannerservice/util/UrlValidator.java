package mohamed.boufous.scannerservice.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class UrlValidator {

    private UrlValidator() {}

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE
    );

    private static final int MAX_URL_LENGTH = 2048;

    public static boolean isValid(String url) {
        if (url == null || url.isBlank() || url.length() > MAX_URL_LENGTH) {
            return false;
        }
        if (!URL_PATTERN.matcher(url).matches()) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null && !host.isBlank() && host.contains(".");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String normalize(String url) {
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://" + trimmed;
        }
        return trimmed;
    }
}
