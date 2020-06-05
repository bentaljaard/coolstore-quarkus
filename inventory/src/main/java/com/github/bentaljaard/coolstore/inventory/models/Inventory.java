package com.github.bentaljaard.coolstore.inventory.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Inventory extends PanacheEntity {
    public String location;
    public int quantity;
    public String link;

}
