package cn.com.crc.ems.finance.agent.storage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

class ObjectReferenceTest {

    @Test
    void rejectsHostPathsAndTraversalSegments() {
        String[] invalidKeys = {"/absolute/file.txt", "C:/absolute/file.txt", "a\\b.txt", "a/../b.txt", "a/./b.txt"};
        for (String invalidKey : invalidKeys) {
            assertThatThrownBy(() -> ObjectReference.requireLogicalKey(invalidKey))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
