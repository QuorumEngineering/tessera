package com.quorum.tessera.data.internal;

import com.quorum.tessera.config.Config;
import com.quorum.tessera.config.ConfigFactory;
import com.quorum.tessera.config.JdbcConfig;
import com.quorum.tessera.data.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.quorum.tessera.data.DataSourceFactory;

@RunWith(Parameterized.class)
public class EncryptedMessageDAOProviderTest {

  private boolean autocreateTables;

  public EncryptedMessageDAOProviderTest(boolean autocreateTables) {
    this.autocreateTables = autocreateTables;
  }

  @Test
  public void defaultConstructorForCoverage() {
    assertThat(new EncryptedMessageDAOProvider()).isNotNull();
  }

  @Test
  public void provides() {
    try (var mockedConfigFactory = mockStatic(ConfigFactory.class);
         var mockedDataSourceFactory = mockStatic(DataSourceFactory.class);
         var mockedPersistence = mockStatic(Persistence.class)) {

      mockedPersistence
        .when(() -> Persistence.createEntityManagerFactory(anyString(), anyMap()))
        .thenReturn(mock(EntityManagerFactory.class));

      Config config = mock(Config.class);
      JdbcConfig jdbcConfig = mock(JdbcConfig.class);
      when(jdbcConfig.isAutoCreateTables()).thenReturn(autocreateTables);
      when(config.getJdbcConfig()).thenReturn(jdbcConfig);

      ConfigFactory configFactory = mock(ConfigFactory.class);
      when(configFactory.getConfig()).thenReturn(config);

      mockedConfigFactory.when(ConfigFactory::create).thenReturn(configFactory);

      mockedDataSourceFactory
        .when(DataSourceFactory::create)
        .thenReturn(mock(DataSourceFactory.class));

      EncryptedMessageDAO result = EncryptedMessageDAOProvider.provider();
      assertThat(result).isNotNull().isExactlyInstanceOf(EncryptedMessageDAOImpl.class);

      mockedPersistence.verify(() -> Persistence.createEntityManagerFactory(anyString(), anyMap()));
      mockedPersistence.verifyNoMoreInteractions();
      EncryptedMessageDAOProvider.provider();
    }
  }

  @Parameterized.Parameters
  public static Collection<Boolean> autoCreateTables() {
    return List.of(true, false);
  }
}
