package cn.com.crc.ems.finance.agent.storage.infrastructure;

import java.io.InputStream;

import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageGateway;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageNotConfiguredException;
import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:43
 */
public final class DisabledObjectStorageGateway implements ObjectStorageGateway {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: objectKey 输入参数。
     * @param: content 输入参数。
     * @param: mediaType 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public ObjectReference put(String objectKey, InputStream content, String mediaType) {
        throw new ObjectStorageNotConfiguredException();
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public InputStream open(ObjectReference reference) {
        throw new ObjectStorageNotConfiguredException();
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @author: yigeyige99
     */
    @Override
    public void delete(ObjectReference reference) {
        throw new ObjectStorageNotConfiguredException();
    }
}
