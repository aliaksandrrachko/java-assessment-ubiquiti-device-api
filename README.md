# Ubiquiti Device API 

[![Java](https://img.shields.io/badge/Java-21-blue)](https://github.com/aliaksandrrachko/java-assessment-ubiquiti-device-api)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-blue)](https://github.com/aliaksandrrachko/java-assessment-ubiquiti-device-api)
[![Build Scan](https://img.shields.io/badge/Build%20Scan-enabled-brightgreen)](https://gradle.com)
[![Spring](https://img.shields.io/badge/Spring-6DB33F?logo=Spring&logoColor=white)](https://spring.io/)

## Overview
Device API for managing network deployments consisting of networking devices (Gateways, Switches, and Access Points).

## Implementation Notes
1. **REST API Service** - Implemented as a Spring Boot REST API
2. **Simplified Model** - Single model class used across all layers (no separate DTOs, domain models, or persistence models) to minimize complexity
3. **Layered Architecture** - Using package-by-layer structure with merged RestController and Service layer
4. **Simple Design** - No complex architectural patterns (hexagonal, clean architecture, etc.)
5. **In-Memory Storage** - Data stored in memory; not persistent between application restarts
6. **Testing** - Unit and integration tests included with meaningful coverage
7. **Development Tools** - Built with assistance from Amazon Q CLI
8. **Build** - Run `./gradlew build` to build the application
9. **Run** - Run `./gradlew bootRun` to start the application (accessible at http://localhost:8080/api)

## Prerequisites
- Java 8 or higher

## Requirements
- Gradle or Maven as build tool, preferably with wrapper included
- Application can be built and run without any additional configuration needed, provide necessary commands for running it
- Feel free to design API as you wish - it can be REST API, CLI application, etc.
- Feel free to choose any 3rd party libraries you find useful

## What will be evaluated
- Completion of requirements
- Code quality & design
- Meaningful test coverage

## Description
Network deployment might consist of several devices.

Networking device types:
- **Gateway** - serves as access point to another network
- **Switch** - connects devices on a computer network
- **Access Point** - connects devices on a computer network via Wi-Fi

Typically, these devices are connected to one another and collectively form a network deployment. Every device on a computer network can be identified by MAC address. If device is attached to another device in same network, it is represented via uplink reference.

## Task
Define and implement Device API, which should support following features:

1. **Registering a device to a network deployment**
   - Input: deviceType, macAddress, uplinkMacAddress

2. **Retrieving all registered devices, sorted by device type**
   - Output: sorted list of devices, where each entry has deviceType and macAddress (sorting order: Gateway > Switch > Access Point)

3. **Retrieving network deployment device by MAC address**
   - Input: macAddress
   - Output: Device entry, which consists of deviceType and macAddress

4. **Retrieving all registered network device topology**
   - Output: Device topology as tree structure, node should be represented as macAddress

5. **Retrieving network device topology starting from a specific device**
   - Input: macAddress
   - Output: Device topology where root node is device with matching macAddress

## Additional notes
- Device may or may not be connected to uplink device

## Submission
- Provide URL to a public repository or send project files as ZIP archive
