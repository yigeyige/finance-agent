package cn.com.crc.ems.finance.agent.harness.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 *
 * @Description: 校验 BusinessGuidanceContext 对场景附件正文数量的边界：1 必读 + 2 请求 = 3。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 11:45:00
 */
class BusinessGuidanceContextTest {

    @Test
    void acceptsThreeSceneAttachmentBodies() {
        assertThatCode(() -> context(bodies(3))).doesNotThrowAnyException();
    }

    @Test
    void rejectsFourSceneAttachmentBodies() {
        assertThatThrownBy(() -> context(bodies(4)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sceneAttachmentBodies is invalid");
    }

    private static BusinessGuidanceContext context(List<SceneSkillAttachmentBody> bodies) {
        return new BusinessGuidanceContext(UUID.randomUUID(), "测试", "说明", null, null, null,
                List.of(), List.of(), List.of(), bodies);
    }

    private static List<SceneSkillAttachmentBody> bodies(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(index -> new SceneSkillAttachmentBody("sample-" + index + ".json", "content-" + index))
                .toList();
    }
}
