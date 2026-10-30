package cn.com.crc.ems.finance.agent.harness.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.support.ExcelTypeEnum;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceAttachment;

/**
 *
 * @Description: 校验业务处理初稿附件并提取只在当前请求内使用的文本投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 10:30:00
 */
public final class BusinessGuidanceAttachmentExtractor {

    static final int MAX_ATTACHMENT_COUNT = 3;
    static final long MAX_ATTACHMENT_BYTES = 1_024L * 1_024L;
    static final int MAX_MODEL_CONTENT_CHARS = 24_000;
    static final long MAX_SCENE_ATTACHMENT_BYTES = 100L * 1_024L;
    private static final int MAX_EXCEL_SHEET_COUNT = 5;
    private static final int MAX_EXCEL_ROWS_PER_SHEET = 200;
    private static final int MAX_EXCEL_COLUMNS_PER_ROW = 50;
    private static final int MAX_EXCEL_CELL_CHARS = 500;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "md", "csv", "json", "xml", "xls", "xlsx");

    /**
     *
     * @description: 将经格式、大小和编码校验的上传文件转换为受限模型文本投影。
     *
     * @param: files 本次初稿请求上传的文件
     * @return: 不包含原始二进制内容的临时附件投影
     * @throws: BusinessGuidanceAttachmentException 文件不符合受限附件规则时抛出
     * @author: yigeyige99
     */
    public List<BusinessGuidanceAttachment> extract(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.size() > MAX_ATTACHMENT_COUNT) {
            throw new BusinessGuidanceAttachmentException("最多可上传 3 个附件");
        }
        List<BusinessGuidanceAttachment> attachments = new ArrayList<>();
        int remaining = MAX_MODEL_CONTENT_CHARS;
        for (MultipartFile file : files) {
            String fileName = requireFileName(file);
            if (file.isEmpty() || file.getSize() <= 0) {
                throw new BusinessGuidanceAttachmentException("附件不能为空");
            }
            if (file.getSize() > MAX_ATTACHMENT_BYTES) {
                throw new BusinessGuidanceAttachmentException("单个附件不能超过 1 MB");
            }
            String text = isExcel(fileName) ? extractExcel(file) : decodeUtf8(file);
            if (text.isBlank()) {
                throw new BusinessGuidanceAttachmentException("附件不能为空");
            }
            boolean truncated = text.length() > remaining;
            String content = text.substring(0, Math.min(text.length(), remaining));
            if (content.isBlank()) {
                break;
            }
            attachments.add(new BusinessGuidanceAttachment(fileName, content, truncated));
            remaining -= content.length();
            if (remaining == 0) {
                break;
            }
        }
        if (attachments.isEmpty()) {
            throw new BusinessGuidanceAttachmentException("附件内容超出模型分析限制");
        }
        return List.copyOf(attachments);
    }

    /**
     *
     * @description: 提取单个场景附件的完整文本投影，不按模型输入长度截断；单文件不超过 100KB。
     *
     * @param: file 已通过文件名白名单校验的上传文件
     * @return: 可持久化的完整文本投影
     * @throws: BusinessGuidanceAttachmentException 文件超出大小或解析失败时抛出
     * @author: yigeyige99
     */
    public String extractSceneAttachmentText(MultipartFile file) {
        String fileName = requireFileName(file);
        if (file.isEmpty() || file.getSize() <= 0) {
            throw new BusinessGuidanceAttachmentException("附件不能为空");
        }
        if (file.getSize() > MAX_SCENE_ATTACHMENT_BYTES) {
            throw new BusinessGuidanceAttachmentException("单个附件不能超过 100KB");
        }
        String text = isExcel(fileName) ? extractExcel(file) : decodeUtf8(file);
        if (text.isBlank()) {
            throw new BusinessGuidanceAttachmentException("附件不能为空");
        }
        return text;
    }

    /**
     *
     * @description: 校验文件名和扩展名，拒绝路径片段与不受支持的二进制文件。
     *
     * @param: file 浏览器上传的附件
     * @return: 可安全展示给模型的文件名
     * @throws: BusinessGuidanceAttachmentException 文件名或格式不合法时抛出
     * @author: yigeyige99
     */
    private static String requireFileName(MultipartFile file) {
        String fileName = file == null ? null : file.getOriginalFilename();
        if (fileName == null || fileName.isBlank() || fileName.length() > 255 || fileName.contains("/")
                || fileName.contains("\\") || fileName.chars().anyMatch(Character::isISOControl)) {
            throw new BusinessGuidanceAttachmentException("附件文件名不合法");
        }
        int extensionStart = fileName.lastIndexOf('.');
        String extension = extensionStart < 1 ? "" : fileName.substring(extensionStart + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessGuidanceAttachmentException("仅支持 txt、md、csv、json、xls、xlsx 格式附件");
        }
        return fileName;
    }

    /**
     *
     * @description: 使用报告模式解码 UTF-8，避免替换损坏字节后再把内容送入模型。
     *
     * @param: file 已通过大小校验的附件
     * @return: 经过 UTF-8 严格解码的文本
     * @throws: BusinessGuidanceAttachmentException 读取或编码不合法时抛出
     * @author: yigeyige99
     */
    private static String decodeUtf8(MultipartFile file) {
        try {
            return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(file.getBytes())).toString();
        } catch (CharacterCodingException exception) {
            throw new BusinessGuidanceAttachmentException("附件必须使用 UTF-8 文本编码");
        } catch (IOException exception) {
            throw new BusinessGuidanceAttachmentException("附件读取失败，请重新选择后重试");
        }
    }

    /**
     *
     * @description: 判断文件是否需要按 Excel 工作簿流式读取。
     *
     * @param: fileName 已验证的原始文件名
     * @return: 是否为 xls 或 xlsx 文件
     * @author: yigeyige99
     */
    private static boolean isExcel(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return "xls".equals(extension) || "xlsx".equals(extension);
    }

    /**
     *
     * @description: 按经过白名单校验的扩展名固定工作簿类型，避免二进制内容被误判为其他文本格式。
     *
     * @param: fileName 已验证的原始文件名
     * @return: FastExcel 使用的工作簿类型
     * @author: yigeyige99
     */
    private static ExcelTypeEnum resolveExcelType(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).endsWith(".xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX;
    }

    /**
     *
     * @description: 流式提取 Excel 的可见工作表值，不保留原始工作簿或样式数据。
     *
     * @param: file 已通过文件名和大小校验的 Excel 附件
     * @return: 带工作表、表头和行号的受限文本投影
     * @throws: BusinessGuidanceAttachmentException 工作簿不可读或超出结构边界时抛出
     * @author: yigeyige99
     */
    private static String extractExcel(MultipartFile file) {
        ExcelAttachmentReadListener listener = new ExcelAttachmentReadListener();
        try (InputStream inputStream = file.getInputStream();
                ExcelReader excelReader = FastExcel.read(inputStream, listener)
                        .excelType(resolveExcelType(file.getOriginalFilename())).autoCloseStream(false).build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            if (sheets.size() > MAX_EXCEL_SHEET_COUNT) {
                throw new BusinessGuidanceAttachmentException("Excel 工作表不能超过 5 个");
            }
            List<ReadSheet> visibleSheets = sheets.stream()
                    .filter(sheet -> !sheet.isHidden() && !sheet.isVeryHidden()).toList();
            if (visibleSheets.isEmpty()) {
                throw new BusinessGuidanceAttachmentException("Excel 不包含可分析的工作表");
            }
            excelReader.read(visibleSheets);
            return listener.content();
        } catch (BusinessGuidanceAttachmentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessGuidanceAttachmentException("Excel 附件解析失败，请检查文件后重试");
        }
    }

    private static final class ExcelAttachmentReadListener extends AnalysisEventListener<Map<Integer, String>> {

        private final StringBuilder content = new StringBuilder();
        private Integer currentSheetNo;
        private int currentSheetRows;

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            appendSheetIfNeeded(context);
            appendRow("表头", headMap);
        }

        @Override
        public void invoke(Map<Integer, String> row, AnalysisContext context) {
            appendSheetIfNeeded(context);
            if (currentSheetRows >= MAX_EXCEL_ROWS_PER_SHEET) {
                throw new BusinessGuidanceAttachmentException("Excel 每个工作表不能超过 200 行数据");
            }
            currentSheetRows++;
            appendRow("第 " + currentSheetRows + " 行", row);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 解析过程不保存工作簿，仅累积受限文本投影。
        }

        private void appendSheetIfNeeded(AnalysisContext context) {
            Integer sheetNo = context.readSheetHolder().getSheetNo();
            if (sheetNo.equals(currentSheetNo)) {
                return;
            }
            currentSheetNo = sheetNo;
            currentSheetRows = 0;
            appendLine("工作表：" + context.readSheetHolder().getSheetName());
        }

        private void appendRow(String rowName, Map<Integer, String> row) {
            if (row == null || row.isEmpty()) {
                return;
            }
            if (row.size() > MAX_EXCEL_COLUMNS_PER_ROW
                    || row.keySet().stream().anyMatch(column -> column >= MAX_EXCEL_COLUMNS_PER_ROW)) {
                throw new BusinessGuidanceAttachmentException("Excel 每行不能超过 50 列");
            }
            StringBuilder line = new StringBuilder(rowName).append("：");
            boolean hasValue = false;
            for (int column = 0; column < MAX_EXCEL_COLUMNS_PER_ROW; column++) {
                String value = row.get(column);
                if (value == null || value.isBlank()) {
                    continue;
                }
                if (value.length() > MAX_EXCEL_CELL_CHARS) {
                    throw new BusinessGuidanceAttachmentException("Excel 单元格内容不能超过 500 个字符");
                }
                if (hasValue) {
                    line.append("；");
                }
                line.append("列 ").append(column + 1).append('=').append(value);
                hasValue = true;
            }
            if (hasValue) {
                appendLine(line.toString());
            }
        }

        private void appendLine(String line) {
            content.append(line).append('\n');
        }

        private String content() {
            return content.toString();
        }
    }
}
