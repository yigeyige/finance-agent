package cn.com.crc.ems.finance.agent.evidence.application;

import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;

/**
 *
 * @Description: 专业确认写入结果，返回更新后的判断状态和确认记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ProfessionalConfirmationResult(Finding finding, ProfessionalConfirmation confirmation) {
}
