package com.github.bentaljaard.coolstore.catalog.models;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import org.bson.codecs.pojo.annotations.BsonId;

public class Product extends PanacheMongoEntityBase {

    @BsonId
    public Long id;

    public String name;
    public String description;
    public Double price;
}
