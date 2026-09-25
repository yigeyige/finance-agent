package cn.com.crc.ems.finance.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Root application entry for the finance-agent modular monolith.
 */
@SpringBootApplication
public class FinanceAgentApplication {

    private FinanceAgentApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(FinanceAgentApplication.class, args);
    }
}
