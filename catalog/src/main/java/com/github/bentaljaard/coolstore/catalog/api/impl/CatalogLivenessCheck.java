package com.github.bentaljaard.coolstore.catalog.api.impl;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class CatalogLivenessCheck implements HealthCheck{
    @Override
    public HealthCheckResponse call() {
         return HealthCheckResponse.named("Custom liveness check").up().build();
    }
}