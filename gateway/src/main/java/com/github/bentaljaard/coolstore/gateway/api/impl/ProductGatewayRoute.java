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

                // try {
                //         getContext().setTracing(true);
                //     } catch (Exception e) {
                //         e.printStackTrace();
                //     }
        
                JacksonDataFormat productFormat = new ListJacksonDataFormat();
                productFormat.setUnmarshalType(Product.class);

                from("direct:products_getAll")
                .id("productRoute")
                .streamCaching("true")
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker().faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(2000).end()
                        .recipientList(simple("{{catalog.endpoint}}/products?httpMethod=GET")).end()
                .onFallback()
                        .to("direct:productFallback")
                .end()
                .choice()
                        .when(body().isNull())
                        .to("direct:productFallback")
                        .end()
                .unmarshal(productFormat)
                .split(body()).parallelProcessing().streaming()
                .enrich("direct:inventory", new InventoryEnricher())
                .end();

                from("direct:productFallback")
                .log("Invoking product fallback")
                .id("ProductFallbackRoute")
                .transform()
                .constant(Collections.singletonList(new Product(0L, "Unavailable Product", "Unavailable Product", 0, null)));



                from("direct:inventory")
                .id("inventoryRoute")
                .streamCaching("true")
                .setHeader("id", simple("${body.id}")) 
                .setBody(simple("null")).removeHeaders("CamelHttp*")
                .circuitBreaker().faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(2000).end()
                        .recipientList(simple("{{inventory.endpoint}}/availability/${header.id}?httpMethod=GET")).end()
                .onFallback()
                        .to("direct:inventoryFallback") 
                .end()
                .choice().when().simple("${body} == ''")
                        .log("Body is null")
                        .to("direct:inventoryFallback")              
                .end()
                .setHeader("CamelJacksonUnmarshalType", simple(Inventory.class.getName()))
                .unmarshal().json(JsonLibrary.Jackson, Inventory.class);

                from("direct:inventoryFallback")
                .id("inventoryFallbackRoute")
                .log("Inventory fallback invoked")
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

//.recipientList(simple("http4://{{env:CATALOG_ENDPOINT:catalog:8080}}/api/products")).end()