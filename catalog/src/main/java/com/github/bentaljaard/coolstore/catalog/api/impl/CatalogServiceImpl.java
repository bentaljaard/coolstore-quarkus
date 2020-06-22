package com.github.bentaljaard.coolstore.catalog.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.github.bentaljaard.coolstore.catalog.api.CatalogService;
import com.github.bentaljaard.coolstore.catalog.models.Product;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.Logger;

@ApplicationScoped
public class CatalogServiceImpl implements CatalogService {

    private static final Logger LOG = Logger.getLogger("CatalogServiceImpl");

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    @ConfigProperty(name = "sampledatafile")
    String filename;


    @Override
    public List<Product> list() {
        LOG.info("Return product listing");
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
        LOG.info("Added product " + product.id);
        return product;
    }


    /*  This is a hacky method to load test data for demo purposes,
    we simply drop the collection and recreate it, this should not
    be used when taking an application to production */
    @PostConstruct
    void initDB(){
        MongoDatabase db = mongoClient.getDatabase(database);
        String collection = "Product";

        if (collectionExists(db, collection)) {
            //clear it out so that we can load fresh test data for demo
            db.getCollection(collection).drop();
        }
        db.createCollection(collection);

        //Load sample data for demo
        addBulk(dataFromFile(filename));
        LOG.info("Loaded sample product data");
    }


    private boolean collectionExists(MongoDatabase db, String collectionName){
        for(String curCollection : db.listCollectionNames()) {
            if(curCollection.equals(collectionName)){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "serial"})
    private List<Product> dataFromFile(String filename){
        Jsonb jsonb = JsonbBuilder.create();
        List<Product> productList = (List<Product>) jsonb.fromJson(
                CatalogServiceImpl.class.getResourceAsStream("/"+filename),
                new ArrayList<Product>(){}.getClass().getGenericSuperclass());
        return productList;
    }

    private List<Product> addBulk(List<Product> bulkProducts){
        for (Product curProduct : bulkProducts) {
            add(curProduct);
        }
        return bulkProducts;
    }

}
