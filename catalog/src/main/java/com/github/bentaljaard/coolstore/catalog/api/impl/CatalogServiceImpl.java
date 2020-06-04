package com.github.bentaljaard.coolstore.catalog.api.impl;

import com.github.bentaljaard.coolstore.catalog.api.CatalogService;
import com.github.bentaljaard.coolstore.catalog.models.Product;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CatalogServiceImpl implements CatalogService {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    @ConfigProperty(name = "sampledatafile")
    String filename;

    @PostConstruct
    private void initDB(){
        MongoDatabase db = mongoClient.getDatabase(database);
        String collection = "Product";

        if (collectionExists(db, collection)) {
            //clear it out so that we can load fresh test data for demo
            db.getCollection(collection).drop();
        }
        db.createCollection(collection);

        //Load sample data for demo
        addBulk(dataFromFile(filename));
    }
    @Override
    public List<Product> list() {
        return Product.listAll();
    }

    @Override
    public Product add(Product newProduct) {
        Product product = new Product();
        if(newProduct.id != null){
            product.id = newProduct.id;
        } else {
            //generate a new id
            product.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        }

        product.name = newProduct.name;
        product.description = newProduct.description;
        product.price = newProduct.price;

        product.persist();
        product.update();
        return product;
    }



    private boolean collectionExists(MongoDatabase db, String collectionName){
        for(String curCollection : db.listCollectionNames()) {
            if(curCollection.equals(collectionName)){
                return true;
            }
        }
        return false;
    }


    private List<Product> dataFromFile(String filename){
        Jsonb jsonb = JsonbBuilder.create();
        List<Product> productList = (List<Product>) jsonb.fromJson(
                CatalogServiceImpl.class.getResourceAsStream("/"+filename),
                new ArrayList<Product>(){}.getClass().getGenericSuperclass());
        return productList;
    }

    public List<Product> addBulk(List<Product> bulkProducts){
        for (Product curProduct : bulkProducts) {
            add(curProduct);
        }
        return bulkProducts;
    }
}
