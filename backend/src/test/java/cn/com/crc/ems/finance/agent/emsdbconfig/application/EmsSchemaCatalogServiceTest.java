package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;

/** 验证手动同步只有完整读取成功后才替换实例目录。 */
class EmsSchemaCatalogServiceTest {
    private final EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
    private final EmsDatabaseQueryExecutor executor = mock(EmsDatabaseQueryExecutor.class);
    private final EmsSchemaCatalogRepository repository = mock(EmsSchemaCatalogRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-31T10:00:00Z"), ZoneOffset.UTC);
    private final EmsSchemaCatalogService service = new EmsSchemaCatalogService(configService, executor, repository, clock);

    @Test
    void savesSuccessfulEmptyCatalogAsSynchronized() {
        UUID id = UUID.randomUUID();
        var config = mock(EmsDatabaseConnectionConfig.class);
        var empty = new EmsSchemaCatalog(List.of());
        when(configService.requireConfigured(id)).thenReturn(config);
        when(executor.listConfigurationSchema(config)).thenReturn(empty);

        assertThat(service.sync(id)).isEqualTo(new EmsSchemaCatalogRepository.SyncStatus(0, clock.instant()));
        verify(repository).save(id, empty, clock.instant());
    }

    @Test
    void sourceFailureDoesNotReplacePreviousCatalog() {
        UUID id = UUID.randomUUID();
        var config = mock(EmsDatabaseConnectionConfig.class);
        when(configService.requireConfigured(id)).thenReturn(config);
        when(executor.listConfigurationSchema(config)).thenThrow(new IllegalStateException("读取失败"));

        assertThatThrownBy(() -> service.sync(id)).isInstanceOf(EmsSchemaSyncException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void viewsSavedCatalogWithoutReadingSourceDatabase() {
        UUID id = UUID.randomUUID();
        var catalog = new EmsSchemaCatalog(List.of());
        when(repository.find(id)).thenReturn(Optional.of(catalog));

        assertThat(service.view(id)).contains(catalog);
        verify(configService).requireConfigured(id);
        verify(repository).find(id);
        verifyNoInteractions(executor);
    }
}
