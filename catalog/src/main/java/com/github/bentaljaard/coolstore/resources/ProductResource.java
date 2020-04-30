package com.github.bentaljaard.coolstore.resources;

import com.github.bentaljaard.coolstore.models.Product;
import com.github.bentaljaard.coolstore.services.ProductService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService products;

    @GET
    public List<Product> hello() {
        return products.list();
    }

    @POST
    public Product add(Product newProduct) {
        products.add(newProduct);
        return newProduct;
    }

    @POST
    @Path("/bulk")
    public List<Product> addBulk(List<Product> productList) {
        products.addBulk(productList);
        return productList;
    }

}

