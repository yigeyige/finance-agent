package cn.com.crc.ems.finance.agent.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;

/** 防止 Controller/Harness 绕过任务状态应用服务直接持久化任务状态。 */
class TaskStateArchitectureContractTest {

    @Test
    void controllerAndHarnessDoNotDependOnTaskRepository() {
        JavaClasses classes = new ClassFileImporter().importPackages("cn.com.crc.ems.finance.agent");
        noClasses().that().resideInAnyPackage("..controller..", "..harness..")
                .should().dependOnClassesThat().areAssignableTo(InvestigationTaskRepository.class)
                .check(classes);
    }
}
