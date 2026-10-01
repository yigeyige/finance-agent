package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 描述测试业务参考样本的黄金事实状态及其确认边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public enum TestBusinessReferenceState {
    CURRENT,
    FUTURE,
    UNKNOWN,
    NO_MATCH;

    /**
     *
     * @description: 判断该测试样本是否表达当前已确认的事实。
     *
     * @return: 仅当前状态返回 true，其他边界状态均返回 false。
     * @author: yigeyige99
     */
    public boolean isConfirmedCurrent() {
        return this == CURRENT;
    }
}
