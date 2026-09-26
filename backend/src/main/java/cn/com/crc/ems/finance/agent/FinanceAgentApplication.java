package cn.com.crc.ems.finance.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:26
 */
public class FinanceAgentApplication {

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    private FinanceAgentApplication() {
    }

    /**
     *
     * @description: 启动当前应用或诊断程序。
     *
     * @param: args 输入参数。
     * @author: yigeyige99
     */
    public static void main(String[] args) {
        SpringApplication.run(FinanceAgentApplication.class, args);
    }
}
