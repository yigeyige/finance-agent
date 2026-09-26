package cn.com.crc.ems.finance.agent.storage.application;

import java.io.InputStream;

import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:40
 */
public interface ObjectStorageGateway {

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
    ObjectReference put(String objectKey, InputStream content, String mediaType);

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    InputStream open(ObjectReference reference);

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @author: yigeyige99
     */
    void delete(ObjectReference reference);
}
