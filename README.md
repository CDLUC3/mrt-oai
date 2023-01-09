# Merritt OAI-PMH Service

This microservice is part of the [Merritt Preservation System](https://github.com/CDLUC3/mrt-doc).

**This code is deprecated as of Jan 2023.**

## Purpose

This microservice implements the [Open Archives Initiative
Protocol for Metadata Harvesting](https://www.openarchives.org/pmh/) for the Merritt System.

This microservice is used by the [Dryad](https://datadryad.org/) service. 
Additional clients of this service are not expected. 

## Component Diagram
[![Flowchart](https://github.com/CDLUC3/mrt-doc/raw/main/diagrams/oai.mmd.svg)](https://cdluc3.github.io/mrt-doc/diagrams/oai)

## Dependencies

This code depends on the following Merritt Libraries.
- [Merritt Core Library](https://github.com/CDLUC3/mrt-core2)

## For external audiences
This code is not intended to be run apart from the Merritt Preservation System.

See [Merritt Docker](https://github.com/CDLUC3/merritt-docker) for a description of how to build a test instnce of Merritt.

## Build instructions
This code is deployed as a war file. The war file is built on a Jenkins server.

## Test instructions

## Internal Links

### Deployment and Operations at CDL

https://github.com/CDLUC3/mrt-doc-private/blob/main/uc3-mrt-oai.md
