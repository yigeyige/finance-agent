package cn.com.crc.ems.finance.agent.storage.infrastructure;

import java.io.InputStream;

import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageGateway;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageNotConfiguredException;
import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

/** Explicit prod placeholder; it never falls back to a local path or memory store. */
public final class DisabledObjectStorageGateway implements ObjectStorageGateway {

    @Override
    public ObjectReference put(String objectKey, InputStream content, String mediaType) {
        throw new ObjectStorageNotConfiguredException();
    }

    @Override
    public InputStream open(ObjectReference reference) {
        throw new ObjectStorageNotConfiguredException();
    }

    @Override
    public void delete(ObjectReference reference) {
        throw new ObjectStorageNotConfiguredException();
    }
}
