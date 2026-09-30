package cn.com.crc.ems.finance.agent.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 *
 * @Description: 适配 PostgreSQL uuid 列与领域 UUID 值之间的 MyBatis 参数和结果映射。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 00:35:00
 */
@MappedTypes(UUID.class)
@MappedJdbcTypes(value = JdbcType.OTHER, includeNullJdbcType = true)
public class PostgresUuidTypeHandler extends BaseTypeHandler<UUID> {

    /**
     *
     * @description: 以 PostgreSQL uuid 所需的 JDBC OTHER 类型绑定非空 UUID 参数。
     *
     * @param: statement 预编译语句。
     * @param: index 参数索引。
     * @param: parameter 待绑定 UUID。
     * @param: jdbcType MyBatis 声明的 JDBC 类型。
     * @throws: SQLException JDBC 参数绑定失败。
     * @author: yigeyige99
     */
    @Override
    public void setNonNullParameter(PreparedStatement statement, int index, UUID parameter, JdbcType jdbcType)
            throws SQLException {
        statement.setObject(index, parameter, Types.OTHER);
    }

    /**
     *
     * @description: 按列名读取 UUID 结果，兼容驱动返回 UUID 对象或 UUID 文本。
     *
     * @param: resultSet 查询结果集。
     * @param: columnName 列名。
     * @return: UUID 结果，数据库空值返回 null。
     * @throws: SQLException JDBC 读取失败。
     * @author: yigeyige99
     */
    @Override
    public UUID getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return toUuid(resultSet.getObject(columnName));
    }

    /**
     *
     * @description: 按列索引读取 UUID 结果，兼容驱动返回 UUID 对象或 UUID 文本。
     *
     * @param: resultSet 查询结果集。
     * @param: columnIndex 列索引。
     * @return: UUID 结果，数据库空值返回 null。
     * @throws: SQLException JDBC 读取失败。
     * @author: yigeyige99
     */
    @Override
    public UUID getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return toUuid(resultSet.getObject(columnIndex));
    }

    /**
     *
     * @description: 从存储过程输出参数读取 UUID 结果。
     *
     * @param: statement 存储过程语句。
     * @param: columnIndex 输出参数索引。
     * @return: UUID 结果，数据库空值返回 null。
     * @throws: SQLException JDBC 读取失败。
     * @author: yigeyige99
     */
    @Override
    public UUID getNullableResult(CallableStatement statement, int columnIndex) throws SQLException {
        return toUuid(statement.getObject(columnIndex));
    }

    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }
}
