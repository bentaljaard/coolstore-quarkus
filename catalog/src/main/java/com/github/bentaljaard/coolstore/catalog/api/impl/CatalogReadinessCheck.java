package com.github.bentaljaard.coolstore.catalog.api.impl;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class CatalogReadinessCheck implements HealthCheck{
    @Override
    public HealthCheckResponse call() {
        // return HealthCheckResponse.named("Custom readiness check").up().build();
        return HealthCheckResponse.named("Custom readiness check").down().build();
    }
}