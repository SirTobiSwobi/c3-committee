# C3-Committee
This repository contains a contribution of the PhD Thesis *Bootstrapping Explainable Text Categorization in Emergent Knowledge-Domains* by Tobias Eljasik-Swoboda submitted to the University of Hagen, Germany on November 30th, 2020. Date of oral examiniation: March 2nd, 2021. 

Classifier committee using Athlete services and itself implementing the classifier trainer API of the Trainer/Athlete pattern. 

See c3-committee.yml config file for metadata about the microservice. The same metadata can be accesses by calling /metadata of the running service. 

The Dockerfile to see which commands you need to run the service on a Linux machine with Java. 

Or you can just run the Docker container including everything necessary. 

Version change log:

- 0.0.1: copied API using classifier-trainer v1.0.7
- 0.0.2: updated API for /configurations, /models, and /model
- 0.0.3: implemented training using external athletes
- 0.0.4: implemented evaluation for athletes and combined metric. Implemented regular classification.
- 1.0.0: implemented explanation.
- 1.0.1: updated JS library for specificity