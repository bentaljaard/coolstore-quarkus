package com.github.bentaljaard.coolstore.models;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection="product")
public class Product extends PanacheMongoEntity {

    public String name;
    public String description;
    public Double price;
}



