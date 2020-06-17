#!/bin/bash

usage() {
echo "
USAGE
 setup_observability_infra.sh -n <namespace>
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


oc new-project ${namespace}

# Deploy Jaeger instance
oc process -f https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/all-in-one/jaeger-all-in-one-template.yml | oc apply -n ${namespace} -f -

# Deploy Prometheus instance
oc process -f prometheus.yaml --param=NAMESPACE=${namespace} --param=IMAGE_PROMETHEUS=prom/prometheus:master --param=IMAGE_ALERTMANAGER=prom/alertmanager:master| oc apply -n ${namespace} -f -

# Deploy Grafana instance
oc process -f grafana.yaml --param=NAMESPACE=${namespace} --param=IMAGE_PROXY=openshift/oauth-proxy:latest --param=IMAGE_GRAFANA=grafana/grafana:master| oc apply -n ${namespace} -f -
