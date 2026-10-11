package cn.com.crc.ems.finance.agent.harness.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.support.ExcelTypeEnum;

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
    void extractsXlsxAndXlsCellsAsLimitedTextForModelAnalysis() {
        var xlsx = extractor.extract(List.of(excelFile("业务数据.xlsx", ExcelTypeEnum.XLSX)));
        var xls = extractor.extract(List.of(excelFile("业务数据.xls", ExcelTypeEnum.XLS)));

        assertThat(xlsx).singleElement().satisfies(attachment -> {
            assertThat(attachment.content()).contains("工作表：待核验事项", "表头：列 1=事项编号；列 2=处理状态",
                    "第 1 行：列 1=A001；列 2=待核验");
            assertThat(attachment.truncated()).isFalse();
        });
        assertThat(xls).singleElement().satisfies(attachment -> {
            assertThat(attachment.content()).contains("工作表：待核验事项", "第 1 行：列 1=A001；列 2=待核验");
            assertThat(attachment.truncated()).isFalse();
        });
    }

    @Test
    void rejectsUnsupportedFormatCountSizeAndEncoding() {
        assertThatThrownBy(() -> extractor.extract(List.of(file("依据.pdf", "内容"))))
                .hasMessage("仅支持 txt、md、csv、json、xls、xlsx 格式附件");
        assertThatThrownBy(() -> extractor.extract(List.of(file("a.txt", "a"), file("b.txt", "b"),
                file("c.txt", "c"), file("d.txt", "d")))).hasMessage("最多可上传 3 个附件");
        assertThatThrownBy(() -> extractor.extract(List.of(new MockMultipartFile("files", "过大.txt", "text/plain",
                new byte[(int) BusinessGuidanceAttachmentExtractor.MAX_ATTACHMENT_BYTES + 1]))))
                .hasMessage("单个附件不能超过 1 MB");
        assertThatThrownBy(() -> extractor.extract(List.of(new MockMultipartFile("files", "编码.txt", "text/plain",
                new byte[] {(byte) 0xc3, 0x28})))).hasMessage("附件必须使用 UTF-8 文本编码");
        assertThatThrownBy(() -> extractor.extract(List.of(new MockMultipartFile("files", "损坏.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "not-an-excel".getBytes(StandardCharsets.UTF_8)))))
                .hasMessage("Excel 附件解析失败，请检查文件后重试");
    }

    @Test
    void limitsTextSentToModelAcrossAttachments() {
        var attachments = extractor.extract(List.of(file("a.txt", "a".repeat(20_000)), file("b.txt", "b".repeat(20_000))));

        assertThat(attachments).hasSize(2);
        assertThat(attachments.stream().mapToInt(attachment -> attachment.content().length()).sum()).isEqualTo(24_000);
        assertThat(attachments.get(1).truncated()).isTrue();
    }

    @Test
    void rejectsExcelRowsBeyondTheControlledAnalysisLimit() {
        assertThatThrownBy(() -> extractor.extract(List.of(excelFileWithDataRows("超行数.xlsx", ExcelTypeEnum.XLSX, 201))))
                .hasMessage("Excel 每个工作表不能超过 200 行数据");
    }

    @Test
    void rejectsExcelWithMoreThanFiveSheets() {
        assertThatThrownBy(() -> extractor.extract(List.of(excelFileWithSheets("超工作表.xlsx", 6))))
                .hasMessage("Excel 工作表不能超过 5 个");
    }

    /** 创建与 HTTP multipart 解析行为一致的内存文本附件。 */
    private static MockMultipartFile file(String name, String content) {
        return new MockMultipartFile("files", name, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }

    /** 创建真实的 XLS 或 XLSX 文件，验证解析器不会把二进制原样交给模型。 */
    private static MockMultipartFile excelFile(String name, ExcelTypeEnum excelType) {
        return excelFileWithDataRows(name, excelType, 1);
    }

    private static MockMultipartFile excelFileWithDataRows(String name, ExcelTypeEnum excelType, int dataRows) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("事项编号", "处理状态"));
        for (int index = 1; index <= dataRows; index++) {
            rows.add(List.of("A%03d".formatted(index), "待核验"));
        }
        FastExcel.write(outputStream).excelType(excelType).sheet("待核验事项")
                .doWrite(rows);
        return new MockMultipartFile("files", name, "application/vnd.ms-excel", outputStream.toByteArray());
    }

    private static MockMultipartFile excelFileWithSheets(String name, int sheetCount) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ExcelWriter writer = FastExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX).build()) {
            for (int sheet = 0; sheet < sheetCount; sheet++) {
                writer.write(List.of(List.of("事项编号"), List.of("A001")), FastExcel.writerSheet(sheet, "工作表" + sheet).build());
            }
        }
        return new MockMultipartFile("files", name, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray());
    }
}
