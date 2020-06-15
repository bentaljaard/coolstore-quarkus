package com.github.bentaljaard.coolstore.gateway.api.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.bentaljaard.coolstore.gateway.api.GatewayService;
import com.github.bentaljaard.coolstore.gateway.models.Product;

import org.apache.camel.ProducerTemplate;

@ApplicationScoped
public class GatewayServiceImpl implements GatewayService {

    @Inject ProducerTemplate producer;

	@Override
	public List<Product> getProducts() {
        return (List<Product>) producer.requestBody("direct:products_getAll", "");
        // ArrayList<Product> products = new ArrayList();
        // products.add(new Product(0L,"test product", "unavailable product", 0.0, null));
		// return products;
	}
    
}