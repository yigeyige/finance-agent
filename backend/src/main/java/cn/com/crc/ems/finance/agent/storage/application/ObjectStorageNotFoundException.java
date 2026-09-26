package cn.com.crc.ems.finance.agent.storage.application;

/** Indicates that a referenced object is not present in the configured store. */
public final class ObjectStorageNotFoundException extends ObjectStorageException {

    public ObjectStorageNotFoundException() {
        super("Object was not found");
    }
}
