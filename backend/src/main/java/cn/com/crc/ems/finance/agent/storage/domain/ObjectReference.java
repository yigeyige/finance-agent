package cn.com.crc.ems.finance.agent.storage.domain;

import java.util.regex.Pattern;

/** Path-free reference to an object managed by a storage gateway. */
public record ObjectReference(String objectKey, String sha256, long sizeBytes, String mediaType) {

    private static final Pattern KEY_SEGMENT = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]*");
    private static final Pattern SHA256 = Pattern.compile("(?i)[0-9a-f]{64}");

    public ObjectReference {
        objectKey = requireLogicalKey(objectKey);
        if (sha256 == null || !SHA256.matcher(sha256).matches()) {
            throw new IllegalArgumentException("sha256 must be a 64-character hexadecimal digest");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes must not be negative");
        }
        if (mediaType == null || mediaType.isBlank() || mediaType.contains("\r") || mediaType.contains("\n")) {
            throw new IllegalArgumentException("mediaType must be a non-blank single-line value");
        }
        mediaType = mediaType.trim();
    }

    /** Validates and returns a relative logical key; no host path is exposed. */
    public static String requireLogicalKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank() || objectKey.contains("\\")
                || objectKey.startsWith("/") || objectKey.matches("^[A-Za-z]:.*")) {
            throw new IllegalArgumentException("objectKey must be a safe relative logical key");
        }
        String[] segments = objectKey.split("/", -1);
        for (String segment : segments) {
            if (segment.isBlank() || ".".equals(segment) || "..".equals(segment)
                    || !KEY_SEGMENT.matcher(segment).matches()) {
                throw new IllegalArgumentException("objectKey must contain only safe relative segments");
            }
        }
        return objectKey;
    }
}
