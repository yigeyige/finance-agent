-- 到期业务记录可删除其关联运行记录；配置和知识表不参与外键改造。
-- task.investigation_task 与当前步骤/范围修订存在反向外键，统一级联可处理到期父行。
DO $$
DECLARE
    link record;
BEGIN
    FOR link IN
        SELECT c.conname AS constraint_name,
               c.conrelid::regclass AS child_table,
               pg_get_constraintdef(c.oid) AS definition
        FROM pg_constraint c
        JOIN pg_class child_rel ON child_rel.oid = c.conrelid
        JOIN pg_namespace child_ns ON child_ns.oid = child_rel.relnamespace
        JOIN pg_class parent_rel ON parent_rel.oid = c.confrelid
        JOIN pg_namespace parent_ns ON parent_ns.oid = parent_rel.relnamespace
        WHERE c.contype = 'f'
          AND child_ns.nspname IN ('task', 'evidence', 'packet', 'harness')
          AND parent_ns.nspname IN ('task', 'evidence', 'packet', 'harness')
          AND parent_rel.relname NOT IN ('business_scene_skill', 'business_scene_skill_attachment')
    LOOP
        IF position('ON DELETE CASCADE' IN link.definition) > 0 THEN
            CONTINUE;
        END IF;
        IF position('ON DELETE ' IN link.definition) > 0 THEN
            RAISE EXCEPTION 'unexpected delete action on %', link.constraint_name;
        END IF;
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', link.child_table, link.constraint_name);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT %I %s ON DELETE CASCADE',
                       link.child_table, link.constraint_name, link.definition);
    END LOOP;
END;
$$;

-- 普通连接仍不得改删审计事件；清理批次仅在本地事务内开启专用标志。
CREATE OR REPLACE FUNCTION governance.reject_audit_mutation()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'DELETE'
       AND current_setting('finance_agent.retention_cleanup', true) = 'on' THEN
        RETURN OLD;
    END IF;
    RAISE EXCEPTION 'governance.audit_event is append-only';
END;
$$;
