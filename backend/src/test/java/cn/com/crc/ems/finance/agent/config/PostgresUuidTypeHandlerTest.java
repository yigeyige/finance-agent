package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.UUID;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;

/**
 *
 * @Description: 校验 PostgreSQL UUID 类型处理器的参数绑定和结果转换行为。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 00:35:00
 */
class PostgresUuidTypeHandlerTest {

    private final PostgresUuidTypeHandler handler = new PostgresUuidTypeHandler();

    @Test
    void bindsUuidAsPostgresOtherParameter() throws Exception {
        PreparedStatement statement = org.mockito.Mockito.mock(PreparedStatement.class);
        UUID value = UUID.randomUUID();

        handler.setNonNullParameter(statement, 2, value, JdbcType.OTHER);

        verify(statement).setObject(2, value, Types.OTHER);
    }

    @Test
    void readsUuidObjectAndUuidText() throws Exception {
        ResultSet resultSet = org.mockito.Mockito.mock(ResultSet.class);
        UUID value = UUID.randomUUID();
        when(resultSet.getObject("uuid_object")).thenReturn(value);
        when(resultSet.getObject("uuid_text")).thenReturn(value.toString());

        assertThat(handler.getNullableResult(resultSet, "uuid_object")).isEqualTo(value);
        assertThat(handler.getNullableResult(resultSet, "uuid_text")).isEqualTo(value);
    }

    @Test
    void registersHandlerForUuidProperties() {
        MybatisConfiguration configuration = new MybatisConfiguration();

        new MybatisUuidTypeHandlerConfiguration().postgresUuidTypeHandlerCustomizer().customize(configuration);

        assertThat(configuration.getTypeHandlerRegistry().getTypeHandler(UUID.class))
                .isInstanceOf(PostgresUuidTypeHandler.class);
    }
}
