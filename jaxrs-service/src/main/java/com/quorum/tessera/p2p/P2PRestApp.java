package com.quorum.tessera.p2p;

import com.quorum.tessera.api.filter.GlobalFilter;
import com.quorum.tessera.api.filter.IPWhitelistFilter;
import com.quorum.tessera.app.TesseraRestApplication;
import com.quorum.tessera.config.apps.P2PApp;
import com.quorum.tessera.service.locator.ServiceLocator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;

/**
 * The main application that is submitted to the HTTP server Contains all the service classes created by the service
 * locator
 */
@GlobalFilter
@ApplicationPath("/")
public class P2PRestApp extends TesseraRestApplication implements P2PApp {

  private final ServiceLocator serviceLocator;

  private final String contextName;

  public P2PRestApp(final ServiceLocator serviceLocator, final String contextName) {
    this.serviceLocator = Objects.requireNonNull(serviceLocator);
    this.contextName = Objects.requireNonNull(contextName);
  }

  @Override
  public Set<Object> getSingletons() {

    Predicate<Object> isPartyInfoResource = o -> PartyInfoResource.class.isInstance(o);
    Predicate<Object> isIPWhitelistFilter = o -> IPWhitelistFilter.class.isInstance(o);
    Predicate<Object> isTransactionResource = o -> TransactionResource.class.isInstance(o);

    return Stream.concat(
            Stream.of(new ApiResource()),
            serviceLocator.getServices(contextName).stream()
                .filter(Objects::nonNull)
                .filter(o -> Objects.nonNull(o.getClass()))
                .filter(o -> Objects.nonNull(o.getClass().getPackage()))
                .filter(isIPWhitelistFilter.or(isPartyInfoResource).or(isTransactionResource)))
        .collect(Collectors.toSet());
  }
}
