package com.github.bentaljaard.coolstore.gateway.api.impl;

import java.util.Collections;

import com.github.bentaljaard.coolstore.gateway.models.Inventory;
import com.github.bentaljaard.coolstore.gateway.models.Product;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

public class ProductGatewayRoute extends RouteBuilder {


	@Override
	public void configure() throws Exception {

              
                JacksonDataFormat productFormat = new ListJacksonDataFormat();
                productFormat.setUnmarshalType(Product.class);

                
                // Main products api that enriches the response with availabilty details

                from("direct:products_getAll")
                .id("productRoute")
                .streamCaching("true")
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker()        
                        .recipientList(simple("{{catalog.endpoint}}/products?httpMethod=GET")).end()
                        .log("**** Product service instance: ${headers.instanceName}")
                .onFallback().to("direct:productFallback").end()
                .choice()
                        .when(body().isNull()) 
                        //No products returned, default to fallback product
                        .to("direct:productFallback")
                        .end()
                .unmarshal(productFormat)
                .split(body()).parallelProcessing().streaming()
                .enrich("direct:inventory", new InventoryEnricher())
                .end();

                
                // Product fallback route

                from("direct:productFallback")
                .log("Invoking product fallback")
                .id("ProductFallbackRoute")
                .transform()
                .constant(Collections.singletonList(new Product(0L, "Unavailable Product", "Unavailable Product", 0, null)))
                .marshal(productFormat);


                //Retrieve inventory for a product
                from("direct:inventory")
                .id("inventoryRoute")
                .log("Inventory invoked")
                .streamCaching("true")
                .setHeader("id", simple("${body.id}")) 
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker()        
                        .recipientList(simple("{{inventory.endpoint}}/availability/${header.id}?httpMethod=GET")).end()
                        .log("**** Inventory service instance: ${headers.instanceName}")
                .onFallback().to("direct:inventoryFallback").end()
                .log("${body}")
                .choice().when().simple("${body} == '' || ${body} == null")
                        .log("No availability found for the product")
                        .to("direct:inventoryFallback")              
                .end()
                .setHeader("CamelJacksonUnmarshalType", simple(Inventory.class.getName()))
                .unmarshal().json(JsonLibrary.Jackson, Inventory.class);

                //Inventory fallback route
                from("direct:inventoryFallback")
                .id("inventoryFallbackRoute")
                .log("Inventory fallback invoked - setting 0 availability")
                .transform()
                .constant(new Inventory(0L, 0, "Local Store", "http://developers.redhat.com"))
                .marshal().json(JsonLibrary.Jackson, Inventory.class);

		
        }
        
        private class InventoryEnricher implements AggregationStrategy {
               
                @Override
                public Exchange aggregate(Exchange original, Exchange resource) {

                // Add the discovered availability to the product and set it back
                Product p = original.getIn().getBody(Product.class);
                Inventory i = resource.getIn().getBody(Inventory.class);
                p.setAvailability(i);
                original.getMessage().setBody(p);
                log.info("------------------->"+p);
                
                return original;

                }
        }

    
}