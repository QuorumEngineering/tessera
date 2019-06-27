package com.quorum.tessera.admin;

import com.quorum.tessera.api.filter.GlobalFilter;
import com.quorum.tessera.api.filter.IPWhitelistFilter;
import com.quorum.tessera.app.TesseraRestApplication;
import com.quorum.tessera.config.apps.AdminApp;
import com.quorum.tessera.service.locator.ServiceLocator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;

/** An app that allows access to node management resources */
@GlobalFilter
@ApplicationPath("/")
public class AdminRestApp extends TesseraRestApplication implements AdminApp {

  private final ServiceLocator serviceLocator;

  private final String contextName;

  public AdminRestApp(final ServiceLocator serviceLocator, final String contextName) {
    this.serviceLocator = Objects.requireNonNull(serviceLocator);
    this.contextName = Objects.requireNonNull(contextName);
  }

  @Override
  public Set<Object> getSingletons() {

    Predicate<Object> isConfigResource = o -> ConfigResource.class.isInstance(o);
    Predicate<Object> isIPWhitelistFilter = o -> IPWhitelistFilter.class.isInstance(o);

    return serviceLocator.getServices(contextName).stream()
        .filter(Objects::nonNull)
        .filter(o -> Objects.nonNull(o.getClass()))
        .filter(o -> Objects.nonNull(o.getClass().getPackage()))
        .filter(isConfigResource.or(isIPWhitelistFilter))
        .collect(Collectors.toSet());
  }
}
