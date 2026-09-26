package cn.com.crc.ems.finance.agent.storage.application;

/** Indicates that an object exceeds the configured temporary storage limit. */
public final class ObjectStorageLimitExceededException extends ObjectStorageException {

    public ObjectStorageLimitExceededException() {
        super("Object exceeds the configured storage size limit");
    }
}
