package cn.com.crc.ems.finance.agent.storage.application;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:40
 */
public class ObjectStorageException extends RuntimeException {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: message 输入参数。
     * @author: yigeyige99
     */
    public ObjectStorageException(String message) {
        super(message);
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: message 输入参数。
     * @param: cause 输入参数。
     * @author: yigeyige99
     */
    public ObjectStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
