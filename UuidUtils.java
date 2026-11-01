package com.ha.dxl.common.util;

import java.util.UUID;

/**
 * Utility class for UUID operations.
 */
public final class UuidUtils {

    private UuidUtils() {}

    /**
     * Parses a UUID string, returning null if invalid rather than throwing.
     */
    public static UUID parseOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
