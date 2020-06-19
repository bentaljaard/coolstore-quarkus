Red Hat Cool Store Microservice Demo (Quarkus)
===============================================
This is an example demo showing a retail store consisting of several microservices based on Quarkus

It demonstrates how to wire up small microservices into a larger application using microservice architectural principals.


Services
--------
There are several individual microservices and infrastructure components that make up this app:

1. Catalog Service - serves products and prices for retail products
1. Cart Service - manages shopping cart for each customer (TBC)
1. Inventory Service - serves inventory and availability data for retail products
1. Pricing Service - Business rules application for product pricing (TBC)
1. Review Service - for writing and displaying reviews for products (TBC)
1. Rating Service - for rating products (TBC)
1. Coolstore Gateway - serving as an API gateway to the backend services (Partially implemented)
1. Web UI - A frontend (TBC)

![Architecture Screenshot](docs/images/coolstore.png?raw=true "Architecture Diagram")

![Architecture Screenshot](docs/images/store.png?raw=true "CoolStore Online Shop")

