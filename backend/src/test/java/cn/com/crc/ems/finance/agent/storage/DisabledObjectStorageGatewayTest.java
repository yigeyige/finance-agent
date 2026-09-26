package cn.com.crc.ems.finance.agent.storage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageNotConfiguredException;
import cn.com.crc.ems.finance.agent.storage.infrastructure.DisabledObjectStorageGateway;

class DisabledObjectStorageGatewayTest {

    @Test
    void neverFallsBackToLocalStorage() {
        DisabledObjectStorageGateway gateway = new DisabledObjectStorageGateway();

        assertThatThrownBy(() -> gateway.put("file.txt", new ByteArrayInputStream(new byte[]{1}), "text/plain"))
                .isInstanceOf(ObjectStorageNotConfiguredException.class);
    }
}
