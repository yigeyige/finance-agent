package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.JdbcEmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/** 验证 EMS Agent 查询在创建 JDBC 语句前被硬性限制为受控单条 SELECT。 */
class EmsReadOnlySqlGuardTest {
    private final EmsReadOnlySqlGuard guard = new EmsReadOnlySqlGuard();

    @Test
    void allowsQualifiedSelectAgainstApprovedBusinessSchemas() {
        assertThat(guard.validate("SELECT USERID, USERNAME FROM basedata.sys_register_user WHERE USERID = 'E001'"))
                .startsWith("SELECT");
        assertThat(guard.validate("SELECT u.USERID, r.ROLEID FROM basedata.sys_register_user u JOIN basedata.sys_user_role r ON r.USERID = u.USERID"))
                .contains("JOIN basedata.sys_user_role");
    }

    @Test
    void rejectsAllWriteAndSchemaMutationStatementsBeforeJdbcExecution() {
        List<String> rejected = List.of(
                "UPDATE basedata.sys_register_user SET ENABLED = 0",
                "DELETE FROM basedata.sys_register_user",
                "TRUNCATE TABLE basedata.sys_register_user",
                "DROP TABLE basedata.sys_register_user",
                "ALTER TABLE basedata.sys_register_user ADD x int",
                "INSERT INTO basedata.sys_register_user(USERID) VALUES ('E001')",
                "SELECT * FROM basedata.sys_register_user; DELETE FROM basedata.sys_register_user");

        rejected.forEach(sql -> assertThatThrownBy(() -> guard.validate(sql)).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void rejectsCommentsFileOutputAndUnapprovedSchemas() {
        List<String> rejected = List.of(
                "SELECT * FROM basedata.sys_register_user -- bypass",
                "SELECT * INTO OUTFILE '/tmp/a' FROM basedata.sys_register_user",
                "SELECT * FROM mysql.user",
                "SELECT * FROM claim.t_claim_apply",
                "SELECT * FROM xxljob.xxl_job_info",
                "SELECT * FROM arch_db.t_archive");

        rejected.forEach(sql -> assertThatThrownBy(() -> guard.validate(sql)).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void executorRechecksSqlBeforeItCanOpenAConnection() {
        ModelApiKeyCipher cipher = mock(ModelApiKeyCipher.class);
        var executor = new JdbcEmsDatabaseQueryExecutor(cipher, guard);
        var config = new EmsDatabaseConnectionConfig(java.util.UUID.randomUUID(), "EMS", "jdbc:mysql://localhost:3306/ems",
                "readonly", "ciphertext", 5, 10, 20, true, java.util.UUID.randomUUID(), java.time.Instant.now(),
                java.util.UUID.randomUUID(), java.time.Instant.now());

        assertThatThrownBy(() -> executor.execute(config, "DELETE FROM basedata.sys_register_user"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(cipher);
    }
}
