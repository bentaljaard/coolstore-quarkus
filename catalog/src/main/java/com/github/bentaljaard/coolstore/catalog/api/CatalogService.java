package com.github.bentaljaard.coolstore.catalog.api;

import com.github.bentaljaard.coolstore.catalog.models.Product;

import javax.ws.rs.NotFoundException;
import java.util.List;

public interface CatalogService {
    public List<Product> list()
            throws NotFoundException;
    public Product add(Product newProduct);
}
