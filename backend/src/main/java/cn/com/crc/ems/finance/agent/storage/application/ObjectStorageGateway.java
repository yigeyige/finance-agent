package cn.com.crc.ems.finance.agent.storage.application;

import java.io.InputStream;

import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

/** Port for optional large-object storage; P0 decision-package content remains in PostgreSQL. */
public interface ObjectStorageGateway {

    ObjectReference put(String objectKey, InputStream content, String mediaType);

    InputStream open(ObjectReference reference);

    void delete(ObjectReference reference);
}
