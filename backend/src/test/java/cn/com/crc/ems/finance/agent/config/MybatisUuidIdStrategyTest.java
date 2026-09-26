package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;

import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.RuntimeBundleEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.AgentUserEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.RoleBindingEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationStepEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ScopeRevisionEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.StepApprovalEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskLeaseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskPremiseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ToolInvocationEntity;

/** 验证 MyBatis-Plus ASSIGN_UUID 策略覆盖所有持久化实体。 */
class MybatisUuidIdStrategyTest {

    private static final List<Class<?>> PERSISTENCE_ENTITIES = List.of(
            AgentUserEntity.class,
            RoleBindingEntity.class,
            RuntimeBundleEntity.class,
            InvestigationTaskEntity.class,
            ScopeRevisionEntity.class,
            TaskPremiseEntity.class,
            InvestigationStepEntity.class,
            StepApprovalEntity.class,
            TaskLeaseEntity.class,
            ToolInvocationEntity.class);

    @Test
    void allPersistenceIdsUseAssignUuidAndStringStorageType() throws NoSuchFieldException {
        for (Class<?> entityType : PERSISTENCE_ENTITIES) {
            Field idField = java.util.Arrays.stream(entityType.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(TableId.class))
                    .findFirst()
                    .orElseThrow();
            TableId tableId = idField.getAnnotation(TableId.class);
            assertThat(tableId).as(entityType.getSimpleName()).isNotNull();
            assertThat(tableId.type()).as(entityType.getSimpleName()).isEqualTo(IdType.ASSIGN_UUID);
            assertThat(idField.getType()).as(entityType.getSimpleName()).isEqualTo(String.class);
        }
    }

    @Test
    void defaultIdentifierGeneratorProducesDomainRoundTrippableUuidText() {
        String generated = new DefaultIdentifierGenerator().nextUUID(null);

        assertThat(generated).hasSize(32).doesNotContain("-");
        UUID value = PersistenceUuid.toDomain(generated);
        assertThat(PersistenceUuid.toStorage(value)).isEqualTo(value.toString());
    }
}
