package cn.com.crc.ems.finance.agent.harness.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;

/**
 *
 * @Description: 提示词模板的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-23 18:00:00
 */
@TableName("governance.prompt_template")
public class PromptTemplateEntity {
    @TableId(value = "template_code")
    public String templateCode;
    public String content;
    public Instant updatedAt;
}
