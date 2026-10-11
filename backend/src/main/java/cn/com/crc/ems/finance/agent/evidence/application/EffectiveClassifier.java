package cn.com.crc.ems.finance.agent.evidence.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/**
 *
 * @Description: 使用显式检查时钟判断事实生效状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class EffectiveClassifier {

    /**
     * 
     * @description: 按半开区间 [effectiveFrom, effectiveTo) 进行确定性生效分类
     *
     * @param: checkedAt 调用方注入的检查时点
     * @param: effectiveFrom 生效开始时间
     * @param: effectiveTo 生效结束时间，可为空表示无结束时间
     * @return: 当前、未来或未确定分类
     * @author: yigeyige99
     */
    public EffectiveClass classify(Instant checkedAt, Instant effectiveFrom, Instant effectiveTo) {
        if (checkedAt == null || effectiveFrom == null
                || effectiveTo != null && !effectiveFrom.isBefore(effectiveTo)) {
            return EffectiveClass.UNDETERMINED;
        }
        if (checkedAt.isBefore(effectiveFrom)) {
            return EffectiveClass.FUTURE_SCHEDULED;
        }
        if (effectiveTo != null && !checkedAt.isBefore(effectiveTo)) {
            return EffectiveClass.UNDETERMINED;
        }
        return EffectiveClass.CURRENT_EFFECTIVE;
    }
}
