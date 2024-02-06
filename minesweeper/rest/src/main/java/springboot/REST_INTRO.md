# Web server implementation using spring boot

- [Web server implementation using spring boot](#web-server-implementation-using-spring-boot)
  - [Introduction to spring boot in our project](#introduction-to-spring-boot-in-our-project)
  - [Why use a web server for local data storage?](#why-use-a-web-server-for-local-data-storage)
    - [The current scenario](#the-current-scenario)
    - [The bigger picture: future-proofing](#the-bigger-picture-future-proofing)
  - [What changes when we move to remote storage?](#what-changes-when-we-move-to-remote-storage)
    - [Minimal code changes](#minimal-code-changes)
    - [Users don't notice any changes](#users-dont-notice-any-changes)

## Introduction to spring boot in our project

Spring Boot enables us to set up a fully functional web server with minimal effort. In the current phase of our project, this web server runs locally on your machine. When the server receives HTTP requests, it performs actions such as altering or retrieving information from local files.

## Why use a web server for local data storage?

### The current scenario

At first glance, it might appear redundant to introduce a web server when all the data is stored locally. After all, one could argue that this extra layer only complicates a setup that could be straightforward.

### The bigger picture: future-proofing

However, we need to consider the long-term vision for our project. As we progress, we might want to integrate features that require centralized data storage, such as global leaderboards for Minesweeper. Having a Spring Boot web server in place significantly eases the transition to such a setup.

## What changes when we move to remote storage?

### Minimal code changes

The beauty of this architecture lies in its flexibility. When we decide to switch from local to remote storage, the only part of the code that needs to be updated is the Service layer. This allows us to change the underlying storage mechanism without affecting the rest of the application.

### Users don't notice any changes

From the client's perspective, nothing changes. The HTTP requests remain the same, only the responses may vary based on the new data source. For example, a request to view high scores could redirect the user to a centralized leaderboard hosted on a web page, instead of a local file.
