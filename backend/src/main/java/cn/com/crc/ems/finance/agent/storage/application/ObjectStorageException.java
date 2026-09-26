package cn.com.crc.ems.finance.agent.storage.application;

/** Base type for safe, typed object-storage failures. */
public class ObjectStorageException extends RuntimeException {

    public ObjectStorageException(String message) {
        super(message);
    }

    public ObjectStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
