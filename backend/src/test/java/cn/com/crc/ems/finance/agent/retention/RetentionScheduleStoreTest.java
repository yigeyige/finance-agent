package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RetentionScheduleStoreTest {
    @TempDir Path root;

    @Test
    void missingConfigurationUsesSundayAtTwoAndUpdatedValueSurvivesReload() throws Exception {
        RetentionScheduleStore store = new RetentionScheduleStore(root);
        assertThat(store.read()).isEqualTo(new RetentionScheduleStore.Schedule(7, "02:00"));

        store.write(new RetentionScheduleStore.Schedule(3, "14:25"));

        assertThat(new RetentionScheduleStore(root).read())
                .isEqualTo(new RetentionScheduleStore.Schedule(3, "14:25"));
    }

    @Test
    void invalidWeeklyTimesAreRejectedBeforeWriting() {
        RetentionScheduleStore store = new RetentionScheduleStore(root);
        assertThatThrownBy(() -> store.write(new RetentionScheduleStore.Schedule(0, "02:00")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.write(new RetentionScheduleStore.Schedule(7, "24:00")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.write(new RetentionScheduleStore.Schedule(7, "2:00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void corruptConfigurationFailsClosed() throws Exception {
        Files.writeString(root.resolve("retention-cleanup.json"), "null");
        assertThatThrownBy(() -> new RetentionScheduleStore(root).read())
                .isInstanceOf(IOException.class);
    }

    @Test
    void legacyWeeklyFileRemainsReadableAndMonthlySettingsSurviveReload() throws Exception {
        Files.writeString(root.resolve("retention-cleanup.json"), "{\"dayOfWeek\":7,\"time\":\"02:00\"}");
        RetentionScheduleStore store = new RetentionScheduleStore(root);
        assertThat(store.read()).isEqualTo(new RetentionScheduleStore.Schedule(7, "02:00"));

        store.write(new RetentionScheduleStore.Schedule(
                RetentionScheduleStore.Frequency.MONTHLY, null, 15, "06:20"));
        assertThat(new RetentionScheduleStore(root).read()).isEqualTo(new RetentionScheduleStore.Schedule(
                RetentionScheduleStore.Frequency.MONTHLY, null, 15, "06:20"));
    }

    @Test
    void monthlyScheduleRequiresDayAvailableInEveryMonth() {
        assertThatThrownBy(() -> new RetentionScheduleStore.Schedule(
                RetentionScheduleStore.Frequency.MONTHLY, null, 29, "02:00"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
