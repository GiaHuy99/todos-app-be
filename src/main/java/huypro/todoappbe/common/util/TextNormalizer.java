package huypro.todoappbe.common.util;

import org.springframework.stereotype.Component;

@Component
public class TextNormalizer {

    public String normalizeRequired(String value, String fieldLabel) {
        if (value == null) {
            throw new IllegalArgumentException(fieldLabel + " is required.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldLabel + " is required.");
        }

        return trimmed;
    }

    public String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
