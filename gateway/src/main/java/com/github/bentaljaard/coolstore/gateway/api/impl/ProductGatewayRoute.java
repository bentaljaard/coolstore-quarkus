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
import org.apache.camel.spi.CircuitBreakerConstants;

public class ProductGatewayRoute extends RouteBuilder {


	@Override
	public void configure() throws Exception {

              
                JacksonDataFormat productFormat = new ListJacksonDataFormat();
                productFormat.setUnmarshalType(Product.class);

                
                // Main products api that enriches the response with availabilty details

                from("direct:products_getAll")
                .id("productRoute")
                .to("microprofile-metrics:timer:productRoute.timer?action=start")
                .streamCaching("true")
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker()       
                        .toD("{{catalog.endpoint}}/products?httpMethod=GET")
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
                .end()
                .to("microprofile-metrics:timer:productRoute.timer?action=stop");

                
                // Product fallback route

                from("direct:productFallback")
                .id("productFallbackRoute")
                .log("Invoking product fallback")
                .transform()
                .constant(Collections.singletonList(new Product(0L, "Unavailable Product", "Unavailable Product", 0, null)))
                .marshal(productFormat);


                //Retrieve inventory for a product
                from("direct:inventory")
                .id("inventoryRoute")
                .to("microprofile-metrics:timer:inventoryRoute.timer?action=start")
                .log("Inventory invoked to check availability for ${body.id}")
                .streamCaching("true")
                .setHeader("id", simple("${body.id}")) 
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker().faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(2000).end()        
                        .toD("{{inventory.endpoint}}/availability/${header.id}?httpMethod=GET&socketTimeout=20005")
                        .log("**** Inventory service instance: ${headers.instanceName}")
                .onFallback()
                        .log("==== ${exception.message}")
                        .log("==== CircuitBreakerOpen: ${exchangeProperty" + CircuitBreakerConstants.RESPONSE_SHORT_CIRCUITED + "}")
                        .to("direct:inventoryFallback")
                        .end()
                
                .choice().when().simple("${body} == '' || ${body} == null")
                        .log("No availability found for the product ${header.id}")
                        .to("direct:inventoryFallback")              
                .end()
                .setHeader("CamelJacksonUnmarshalType", simple(Inventory.class.getName()))
                .unmarshal().json(JsonLibrary.Jackson, Inventory.class)
                .to("microprofile-metrics:timer:inventoryRoute.timer?action=stop");

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
                
                return original;

                }
        }

    
}