package cn.com.crc.ems.finance.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Root application entry for the finance-agent modular monolith.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FinanceAgentApplication {

    private FinanceAgentApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(FinanceAgentApplication.class, args);
    }
}
