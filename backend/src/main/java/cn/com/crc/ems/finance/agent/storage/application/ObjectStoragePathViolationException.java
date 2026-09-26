package cn.com.crc.ems.finance.agent.storage.application;

/** Indicates that a logical object key is not safely relative to the configured root. */
public final class ObjectStoragePathViolationException extends ObjectStorageException {

    public ObjectStoragePathViolationException() {
        super("Object storage key violates the relative path policy");
    }
}
