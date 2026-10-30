package cn.com.crc.ems.finance.agent.scenario.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.scenario.domain.SceneSkillAttachment;

/**
 *
 * @Description: 业务场景 Skill 附件的持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
public interface SceneSkillAttachmentRepository {

    /** 按场景读取全部附件。 */
    List<SceneSkillAttachment> findBySkillId(UUID skillId);

    /** 统计场景附件数量。 */
    int countBySkillId(UUID skillId);

    /** 按场景与文件名读取附件正文，供模型按需注入。 */
    List<SceneSkillAttachment> findBySkillIdAndFileNames(UUID skillId, List<String> fileNames);

    /** 新增附件。 */
    void insert(SceneSkillAttachment attachment);

    /** 删除场景下的指定附件，返回是否删除成功。 */
    boolean deleteById(UUID skillId, UUID attachmentId);
}
