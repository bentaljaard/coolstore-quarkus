package com.github.bentaljaard.coolstore.gateway.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Product {

    public Long id;
    public String name;
    public String description;
    public double price;
    public Inventory availability;
    
    public Product() {
		super();
	}
	public Product(Long id, String name, String description, double price, Inventory availability) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.availability = availability;
	}

	
	public Inventory getAvailability() {
		return availability;
	}

	public void setAvailability(Inventory availability) {
		this.availability = availability;
	}

	public Long getId() {
		return id;
	}

	public void setid(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String toString() {
		return ("Product toString: name:" + name + " id:" + id + " price:" + price + " description:" + description + " Availability:" + availability.toString());
	}
}