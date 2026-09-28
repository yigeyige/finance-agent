package cn.com.crc.ems.finance.agent.harness.infrastructure;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.harness.domain.WorkspaceRef;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.CheckpointEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.PlanRevisionEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.SessionStateEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.WorkspaceRefEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** Harness 领域对象与 MyBatis-Plus Entity 的显式映射。 */
public final class MybatisHarnessMappings {

    private MybatisHarnessMappings() {
    }

    /** 将 session Entity 映射为领域对象。 */
    public static SessionState toDomain(SessionStateEntity entity) {
        return new SessionState(PersistenceUuid.toDomain(entity.sessionId), entity.taskId, entity.userId,
                entity.harnessVersion, state(entity.stateSchemaVersion, entity.stateRef, entity.stateDigest),
                entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域 session 映射为 Entity。 */
    public static SessionStateEntity toEntity(SessionState value) {
        SessionStateEntity entity = new SessionStateEntity();
        entity.sessionId = PersistenceUuid.toStorage(value.sessionId());
        entity.taskId = value.taskId();
        entity.userId = value.userId();
        entity.harnessVersion = value.harnessVersion();
        entity.stateSchemaVersion = value.stateRef().schemaVersion();
        entity.stateRef = value.stateRef().json();
        entity.stateDigest = value.stateRef().digest();
        entity.createdBy = value.createdBy();
        entity.createdAt = value.createdAt();
        entity.updatedBy = value.updatedBy();
        entity.updatedAt = value.updatedAt();
        return entity;
    }

    /** 将 checkpoint Entity 映射为领域对象。 */
    public static Checkpoint toDomain(CheckpointEntity entity) {
        return new Checkpoint(PersistenceUuid.toDomain(entity.checkpointId), entity.sessionId, entity.taskId,
                entity.userId, entity.stepId, entity.scopeRevisionId, entity.approvalId, entity.runtimeBundleId,
                entity.checkpointType,
                state(entity.stateSchemaVersion, entity.stateRef, entity.stateDigest), entity.createdBy,
                entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域 checkpoint 映射为 Entity。 */
    public static CheckpointEntity toEntity(Checkpoint value) {
        CheckpointEntity entity = new CheckpointEntity();
        entity.checkpointId = PersistenceUuid.toStorage(value.checkpointId());
        entity.sessionId = value.sessionId();
        entity.taskId = value.taskId();
        entity.userId = value.userId();
        entity.stepId = value.stepId();
        entity.scopeRevisionId = value.scopeRevisionId();
        entity.approvalId = value.approvalId();
        entity.runtimeBundleId = value.runtimeBundleId();
        entity.checkpointType = value.checkpointType();
        entity.stateSchemaVersion = value.stateRef().schemaVersion();
        entity.stateRef = value.stateRef().json();
        entity.stateDigest = value.stateRef().digest();
        entity.createdBy = value.createdBy();
        entity.createdAt = value.createdAt();
        entity.updatedBy = value.updatedBy();
        entity.updatedAt = value.updatedAt();
        return entity;
    }

    /** 将计划修订 Entity 映射为领域对象。 */
    public static PlanRevision toDomain(PlanRevisionEntity entity) {
        return new PlanRevision(PersistenceUuid.toDomain(entity.planRevisionId), entity.sessionId, entity.taskId,
                entity.userId, entity.revisionNo, state(entity.planSchemaVersion, entity.planRef, entity.planDigest),
                entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域计划修订映射为 Entity。 */
    public static PlanRevisionEntity toEntity(PlanRevision value) {
        PlanRevisionEntity entity = new PlanRevisionEntity();
        entity.planRevisionId = PersistenceUuid.toStorage(value.planRevisionId());
        entity.sessionId = value.sessionId();
        entity.taskId = value.taskId();
        entity.userId = value.userId();
        entity.revisionNo = value.revisionNo();
        entity.planSchemaVersion = value.planRef().schemaVersion();
        entity.planRef = value.planRef().json();
        entity.planDigest = value.planRef().digest();
        entity.createdBy = value.createdBy();
        entity.createdAt = value.createdAt();
        entity.updatedBy = value.updatedBy();
        entity.updatedAt = value.updatedAt();
        return entity;
    }

    /** 将 workspace 引用 Entity 映射为领域对象。 */
    public static WorkspaceRef toDomain(WorkspaceRefEntity entity) {
        return new WorkspaceRef(PersistenceUuid.toDomain(entity.workspaceRefId), entity.sessionId, entity.taskId,
                entity.userId, entity.relativePath, entity.objectRef, entity.contentDigest, entity.mediaType,
                entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域 workspace 引用映射为 Entity。 */
    public static WorkspaceRefEntity toEntity(WorkspaceRef value) {
        WorkspaceRefEntity entity = new WorkspaceRefEntity();
        entity.workspaceRefId = PersistenceUuid.toStorage(value.workspaceRefId());
        entity.sessionId = value.sessionId();
        entity.taskId = value.taskId();
        entity.userId = value.userId();
        entity.relativePath = value.relativePath();
        entity.objectRef = value.objectRef();
        entity.contentDigest = value.contentDigest();
        entity.mediaType = value.mediaType();
        entity.createdBy = value.createdBy();
        entity.createdAt = value.createdAt();
        entity.updatedBy = value.updatedBy();
        entity.updatedAt = value.updatedAt();
        return entity;
    }

    private static StateReference state(String schemaVersion, String json, String digest) {
        return new StateReference(schemaVersion, json, digest);
    }
}
