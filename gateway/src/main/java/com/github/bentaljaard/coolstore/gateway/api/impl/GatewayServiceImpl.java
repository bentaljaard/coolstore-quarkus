package com.github.bentaljaard.coolstore.gateway.api.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.bentaljaard.coolstore.gateway.api.GatewayService;
import com.github.bentaljaard.coolstore.gateway.models.Product;

import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.opentracing.Traced;

@Traced
@ApplicationScoped
public class GatewayServiceImpl implements GatewayService {

    @Inject ProducerTemplate producer;

    @Override
    @SuppressWarnings("unchecked")
	public List<Product> getProducts() {
        return (List<Product>) producer.requestBody("direct:products_getAll", "");
       
	}
    
}