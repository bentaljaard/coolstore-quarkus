#!/bin/bash

usage() {
echo "
USAGE
 setup_coolstore_infra.sh -n <namespace>
 switches:
   -n: namespace to deploy infra
"
exit 1
}

while getopts 'n:' flag; do
  case "${flag}" in
    n) namespace="${OPTARG}" ;;
    *) usage ;;
  esac
done

if [ -z ${namespace} ]; then
	usage
fi

##################################
# Catalog Service Setup
##################################

# Deploy MongoDB instance for catalog service
oc new-app --template=mongodb-ephemeral --param=MONGODB_DATABASE=catalog -n ${namespace}

# Create binary builds to allow us to deploy from local environment
oc new-build --binary --name=coolstore-catalog -l app=coolstore-catalog -n ${namespace}
oc patch bc/coolstore-catalog -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.jvm"}}}}'
oc new-build --binary --name=coolstore-catalog-native -l app=coolstore-catalog --to='coolstore-catalog' -n ${namespace}
oc patch bc/coolstore-catalog-native -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'


# Create deployment
oc new-app -n ${namespace} --image-stream=coolstore-catalog:latest -l app=coolstore-catalog --allow-missing-imagestream-tags=true --allow-missing-images=true
oc patch dc/coolstore-catalog -n ${namespace} -p '{"spec":{"template":{"spec":{"containers":[{"name":"coolstore-catalog","env":[{"name":"QUARKUS_MONGODB_DATABASE", "valueFrom":{"secretKeyRef":{"key":"database-name","name":"mongodb"}}},{"name":"QUARKUS_MONGODB_CREDENTIALS_USERNAME", "valueFrom":{"secretKeyRef":{"key":"database-user","name":"mongodb"}}},{"name":"QUARKUS_MONGODB_CREDENTIALS_PASSWORD", "valueFrom":{"secretKeyRef":{"key":"database-password","name":"mongodb"}}},{"name":"QUARKUS_MONGODB_CONNECTION_STRING", "value":"mongodb://mongodb:27017"},{"name":"QUARKUS_HTTP_PORT", "value":"8080"},{"name":"QUARKUS_JAEGER_ENDPOINT", "value":"http://jaeger-collector:14268/api/traces"}]}]}}}}'
oc expose dc/coolstore-catalog -n ${namespace} --port=8080
oc expose svc/coolstore-catalog -n ${namespace} 

# To start jvm build from your machine:
# mvn clean package
# oc start-build coolstore-catalog --from-dir=. --follow

# To start native build from your machine:
# mvn clean package -Pnative
# oc start-build coolstore-catalog-native --from-dir=. --follow


##################################
# Inventory Service Setup
##################################

# Deploy PostgreSql database instance for inventory service
oc new-app --template=postgresql-ephemeral --param=POSTGRESQL_DATABASE=inventory -n ${namespace}

# Create binary builds to allow us to deploy from local environment
oc new-build --binary --name=coolstore-inventory -l app=coolstore-inventory -n ${namespace}
oc patch bc/coolstore-inventory -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.jvm"}}}}'
oc new-build --binary --name=coolstore-inventory-native -l app=coolstore-inventory --to='coolstore-inventory' -n ${namespace}
oc patch bc/coolstore-inventory-native -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'

# Create deployment
oc new-app -n ${namespace} --image-stream=coolstore-inventory:latest -l app=coolstore-inventory --allow-missing-imagestream-tags=true --allow-missing-images=true
oc patch dc/coolstore-inventory -n ${namespace} -p '{"spec":{"template":{"spec":{"containers":[{"name":"coolstore-inventory","env":[{"name":"QUARKUS_DATASOURCE_USERNAME", "valueFrom":{"secretKeyRef":{"key":"database-user","name":"postgresql"}}},{"name":"QUARKUS_DATASOURCE_PASSWORD", "valueFrom":{"secretKeyRef":{"key":"database-password","name":"postgresql"}}},{"name":"QUARKUS_DATASOURCE_JDBC_URL", "value":"jdbc:postgresql://postgresql:5432/postgres"},{"name":"QUARKUS_HTTP_PORT", "value":"8080"},{"name":"QUARKUS_JAEGER_ENDPOINT", "value":"http://jaeger-collector:14268/api/traces"}]}]}}}}'
oc expose dc/coolstore-inventory -n ${namespace} --port=8080
oc expose svc/coolstore-inventory -n ${namespace} 

# To start jvm build from your machine:
# mvn clean package
# oc start-build coolstore-inventory --from-dir=. --follow

# To start native build from your machine:
# mvn clean package -Pnative
# oc start-build coolstore-inventory-native --from-dir=. --follow


##################################
# Gateway Service Setup
##################################

# Create binary builds to allow us to deploy from local environment
oc new-build --binary --name=coolstore-gateway -l app=coolstore-gateway -n ${namespace}
oc patch bc/coolstore-gateway -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.jvm"}}}}'
oc new-build --binary --name=coolstore-gateway-native -l app=coolstore-gateway --to='coolstore-gateway' -n ${namespace}
oc patch bc/coolstore-gateway-native -n ${namespace} -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'

# Create deployment
oc new-app -n ${namespace} --image-stream=coolstore-gateway:latest -l app=coolstore-gateway --allow-missing-imagestream-tags=true --allow-missing-images=true
oc patch dc/coolstore-gateway -n ${namespace} -p '{"spec":{"template":{"spec":{"containers":[{"name":"coolstore-gateway","env":[{"name":"QUARKUS_HTTP_PORT", "value":"8080"},{"name":"CATALOG_ENDPOINT", "value":"http://coolstore-catalog:8080"},{"name":"INVENTORY_ENDPOINT", "value":"http://coolstore-inventory:8080"},{"name":"QUARKUS_JAEGER_ENDPOINT", "value":"http://jaeger-collector:14268/api/traces"}]}]}}}}'
oc expose dc/coolstore-gateway -n ${namespace} --port=8080
oc expose svc/coolstore-gateway -n ${namespace} 

# To start jvm build from your machine:
# mvn clean package
# oc start-build coolstore-gateway --from-dir=. --follow

# To start native build from your machine:
# mvn clean package -Pnative
# oc start-build coolstore-gateway-native --from-dir=. --follow


