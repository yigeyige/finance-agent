package cn.com.crc.ems.finance.agent.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture guard for the modular monolith package boundaries.
 *
 * <p>These rules intentionally fail when a future domain class reaches a web,
 * database, Harness, or infrastructure implementation.</p>
 */
@AnalyzeClasses(packages = "cn.com.crc.ems.finance.agent")
class ModuleArchitectureTest {

    private static final String DOMAIN_PACKAGE = "cn.com.crc.ems.finance.agent..domain..";
    private static final String INFRASTRUCTURE_PACKAGE =
            "cn.com.crc.ems.finance.agent..infrastructure..";

    @ArchTest
    static final ArchRule domain_must_not_depend_on_external_adapters = noClasses()
            .that().resideInAnyPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.web..",
                    "org.springframework.jdbc..",
                    "org.springframework.data..",
                    "jakarta.persistence..",
                    "javax.persistence..",
                    "io.agentscope.harness..",
                    "io.agentscope.core.state..",
                    INFRASTRUCTURE_PACKAGE);

    @ArchTest
    static final ArchRule domain_must_not_depend_on_harness_state = noClasses()
            .that().resideInAnyPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "io.agentscope.harness.agent..",
                    "io.agentscope.core.state..",
                    "io.agentscope.core.permission..");

    @ArchTest
    static void architecture_rules_are_evaluated(JavaClasses classes) {
        domain_must_not_depend_on_external_adapters.check(classes);
        domain_must_not_depend_on_harness_state.check(classes);
    }
}
