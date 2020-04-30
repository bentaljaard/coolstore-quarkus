package com.github.bentaljaard.coolstore.services;

import com.github.bentaljaard.coolstore.models.Product;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductService {


    public List<Product> list(){
        List<Product> products = Product.listAll();
        return products;
    }

    public Product add(Product newProduct){
        Product product = new Product();
        product.name = newProduct.name;
        product.description = newProduct.description;
        product.price = newProduct.price;

        product.persist();
        product.update();

        return product;
    }

    public List<Product> addBulk(List<Product> bulkProducts){
        for (Product curProduct : bulkProducts) {
            add(curProduct);
        }

        return bulkProducts;
    }




}
