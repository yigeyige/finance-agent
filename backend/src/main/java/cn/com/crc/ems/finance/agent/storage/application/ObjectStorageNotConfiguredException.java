package cn.com.crc.ems.finance.agent.storage.application;

/** Indicates that no formal storage adapter is configured for the current environment. */
public final class ObjectStorageNotConfiguredException extends ObjectStorageException {

    public ObjectStorageNotConfiguredException() {
        super("Object storage adapter is not configured");
    }
}
