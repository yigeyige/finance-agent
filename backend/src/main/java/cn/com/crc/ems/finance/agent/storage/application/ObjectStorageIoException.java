package cn.com.crc.ems.finance.agent.storage.application;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:41
 */
public final class ObjectStorageIoException extends ObjectStorageException {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: cause 输入参数。
     * @author: yigeyige99
     */
    public ObjectStorageIoException(Throwable cause) {
        super("Object storage I/O failed", cause);
    }
}
