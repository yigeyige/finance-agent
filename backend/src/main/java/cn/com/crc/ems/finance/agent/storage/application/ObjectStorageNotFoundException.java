package cn.com.crc.ems.finance.agent.storage.application;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:47
 */
public final class ObjectStorageNotFoundException extends ObjectStorageException {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    public ObjectStorageNotFoundException() {
        super("Object was not found");
    }
}
