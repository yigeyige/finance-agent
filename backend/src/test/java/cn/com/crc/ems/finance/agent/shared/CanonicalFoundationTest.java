package cn.com.crc.ems.finance.agent.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.governance.domain.AuditDigest;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 共享 JSON、摘要、UUID 和 UTC 时间的固定测试向量。 */
class CanonicalFoundationTest {

    @Test
    void canonicalJsonUsesStableObjectAndNumberRules() {
        CanonicalJson value = CanonicalJson.of(
                "{\"b\":2,\"a\":1.0,\"zero\":0.00,\"unicode\":\"雪\",\"none\":null,\"array\":[2,1]}");

        assertThat(value.value()).isEqualTo("{\"a\":1,\"array\":[2,1],\"b\":2,\"none\":null,\"unicode\":\"雪\",\"zero\":0}");
        assertThat(value.sha256()).isEqualTo("70c2d5b71a31af8cd0b58a458346ec28230b53832e2f7a83d50a2d4884e42775");
        assertThat(CanonicalJson.of("{\"array\":[1,2],\"a\":1}").sha256())
                .isNotEqualTo(CanonicalJson.of("{\"array\":[2,1],\"a\":1}").sha256());
    }

    @Test
    void invalidNumbersAndTrailingTokensAreRejected() {
        assertThatThrownBy(() -> CanonicalJson.of("{\"value\":NaN}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CanonicalJson.of("{} {}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(CanonicalJson.of("null").value()).isEqualTo("null");
    }

    @Test
    void uuidAndClockAreExplicitlyRoundTrippable() {
        UUID value = UuidGenerator.newUuid();
        assertThat(UuidGenerator.parse(PersistenceUuid.toStorage(value))).isEqualTo(value);
        assertThat(UuidGenerator.parse(value.toString().replace("-", ""))).isEqualTo(value);

        Clock fixed = Clock.fixed(Instant.parse("2026-09-27T12:34:56Z"), ZoneOffset.UTC);
        assertThat(UtcTime.now(fixed)).isEqualTo(Instant.parse("2026-09-27T12:34:56Z"));
        assertThat(UtcTime.formatApi(UtcTime.now(fixed))).isEqualTo("2026-09-27T12:34:56Z");
        assertThat(UtcTime.parseApi("2026-09-27T20:34:56+08:00"))
                .isEqualTo(Instant.parse("2026-09-27T12:34:56Z"));
    }

    @Test
    void auditDigestDelegatesToSharedCanonicalization() {
        VersionedJson payload = new VersionedJson("audit-payload-v1", "{\"b\":2,\"a\":1.0}");
        assertThat(AuditDigest.payloadDigest(payload)).isEqualTo(CanonicalJson.of(payload.json()).sha256());
        assertThat(Sha256Digest.hex("finance-agent")).hasSize(64);
    }
}
