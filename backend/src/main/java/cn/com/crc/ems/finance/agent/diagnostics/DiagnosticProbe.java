package cn.com.crc.ems.finance.agent.diagnostics;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:33
 */
public interface DiagnosticProbe {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    String name();

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    DiagnosticComponentStatus check();
}
