package cn.com.crc.ems.finance.agent.harness.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/** 验证业务处理附件只以受限 UTF-8 文本投影进入模型调用。 */
class BusinessGuidanceAttachmentExtractorTest {

    private final BusinessGuidanceAttachmentExtractor extractor = new BusinessGuidanceAttachmentExtractor();

    @Test
    void extractsSupportedUtf8TextWithoutPersistingBinaryPayload() {
        var attachments = extractor.extract(List.of(file("依据.md", "人员已离任，需核对审批权限。")));

        assertThat(attachments).singleElement().satisfies(attachment -> {
            assertThat(attachment.fileName()).isEqualTo("依据.md");
            assertThat(attachment.content()).contains("核对审批权限");
            assertThat(attachment.truncated()).isFalse();
        });
    }

    @Test
    void rejectsUnsupportedFormatCountSizeAndEncoding() {
        assertThatThrownBy(() -> extractor.extract(List.of(file("依据.pdf", "内容"))))
                .hasMessage("仅支持 txt、md、csv、json 格式附件");
        assertThatThrownBy(() -> extractor.extract(List.of(file("a.txt", "a"), file("b.txt", "b"),
                file("c.txt", "c"), file("d.txt", "d")))).hasMessage("最多可上传 3 个附件");
        assertThatThrownBy(() -> extractor.extract(List.of(new MockMultipartFile("files", "过大.txt", "text/plain",
                new byte[(int) BusinessGuidanceAttachmentExtractor.MAX_ATTACHMENT_BYTES + 1]))))
                .hasMessage("单个附件不能超过 1 MB");
        assertThatThrownBy(() -> extractor.extract(List.of(new MockMultipartFile("files", "编码.txt", "text/plain",
                new byte[] {(byte) 0xc3, 0x28})))).hasMessage("附件必须使用 UTF-8 文本编码");
    }

    @Test
    void limitsTextSentToModelAcrossAttachments() {
        var attachments = extractor.extract(List.of(file("a.txt", "a".repeat(20_000)), file("b.txt", "b".repeat(20_000))));

        assertThat(attachments).hasSize(2);
        assertThat(attachments.stream().mapToInt(attachment -> attachment.content().length()).sum()).isEqualTo(24_000);
        assertThat(attachments.get(1).truncated()).isTrue();
    }

    /** 创建与 HTTP multipart 解析行为一致的内存文本附件。 */
    private static MockMultipartFile file(String name, String content) {
        return new MockMultipartFile("files", name, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }
}
