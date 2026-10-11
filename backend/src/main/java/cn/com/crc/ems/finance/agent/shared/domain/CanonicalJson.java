package cn.com.crc.ems.finance.agent.shared.domain;

/**
 *
 * @Description: 带确定性 JSON 文本和值摘要的共享值对象。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CanonicalJson(String value) {

    public CanonicalJson {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("canonical JSON must not be blank");
        }
        if (!value.equals(CanonicalJsonCodec.canonicalize(CanonicalJsonCodec.parse(value)))) {
            throw new IllegalArgumentException("value must already be canonical JSON");
        }
    }

    /** 将任意合法 JSON 编码为 canonical JSON。 */
    public static CanonicalJson of(String json) {
        return new CanonicalJson(CanonicalJsonCodec.canonicalize(CanonicalJsonCodec.parse(json)));
    }

    /** 计算 canonical JSON 的 SHA-256 摘要。 */
    public String sha256() {
        return Sha256Digest.hex(value);
    }
}
