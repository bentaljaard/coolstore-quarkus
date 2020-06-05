package com.github.bentaljaard.coolstore.gateway.api;

import java.util.List;

import com.github.bentaljaard.coolstore.gateway.models.Product;

public interface GatewayService {
    public List<Product> getProducts();
}