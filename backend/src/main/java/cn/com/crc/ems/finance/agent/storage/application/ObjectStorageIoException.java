package cn.com.crc.ems.finance.agent.storage.application;

/** Indicates a storage I/O failure without exposing local paths or raw provider messages. */
public final class ObjectStorageIoException extends ObjectStorageException {

    public ObjectStorageIoException(Throwable cause) {
        super("Object storage I/O failed", cause);
    }
}
