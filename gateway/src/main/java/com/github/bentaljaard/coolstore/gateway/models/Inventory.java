package com.github.bentaljaard.coolstore.gateway.models;


public class Inventory {
    public Long id;
    public int quantity;
    public String location;
    public String link;

    public Inventory() {

	}

	public Inventory(Long id, int quantity, String location, String link) {
		this.id = id;
		this.quantity = quantity;
		this.location = location;
		this.link = link;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}


	public String toString() {
		return ("Inventory toString: id:" + id + " q:" + quantity + " loc:" + location + " link:" + link);
	}
}