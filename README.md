Classifier committee using services and itself implementing the classifier trainer API of the athlete/trainer pattern. 

See c3-committee.yml config file for metadata about the microservice. The same metadata can be accesses by calling /metadata of the running service. 

The Dockerfile to see which commands you need to run the service on a Linux machine with Java. 

Or you can just run the Docker container including everything necessary. 

Version change log:

- 0.0.1: copied API using classifier-trainer v1.0.7
- 0.0.2: updated API for /configurations, /models, and /model