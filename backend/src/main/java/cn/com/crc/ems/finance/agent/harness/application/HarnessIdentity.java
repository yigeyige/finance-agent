package cn.com.crc.ems.finance.agent.harness.application;

/** Harness 会话身份，所有会话操作必须显式绑定用户和 session。 */
public record HarnessIdentity(String userId, String sessionId) {

    public HarnessIdentity {
        requireSafeId(userId, "userId");
        requireSafeId(sessionId, "sessionId");
    }

    private static void requireSafeId(String value, String field) {
        if (value == null || !value.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,99}")) {
            throw new IllegalArgumentException(field + " must be a non-blank safe identifier");
        }
    }
}
