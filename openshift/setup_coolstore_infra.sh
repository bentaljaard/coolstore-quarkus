#!/bin/bash

usage() {
echo "
USAGE
 setup_coolstore_infra.sh -n <namespace>
 switches:
   -n: namespace to deploy coolstore infra
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

ensure_namespace_exists() {
  oc get project $1
  if [ $? -eq 0 ]
  then
    echo "Namespace exists"
  else
    echo "Namespace does not exist, creating..."
    oc new-project $1
  fi

  if [ $? -eq 0 ]
  then
    return 0
  fi  
}

ensure_namespace_exists ${namespace}

# Deploy catalog database
oc new-app --template=mongodb-ephemeral --param=MONGODB_DATABASE=catalog -n ${namespace}

# Deploy Inventory database
oc new-app --template=postgresql-ephemeral --param=POSTGRESQL_DATABASE=inventory -n ${namespace}