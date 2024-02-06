# REST service documentation

This document outlines the structure and usage of HTTP requests supported by our REST-service. The service currently facilitates operations related to the highscore list of our application.

## Table of contents

- [Server type](#server-type)
- [GET requests](#get-requests)
  - [Retrieve all highscores](#retrieve-all-highscores)
    - [Example of response body](#example-of-response-body)
- [POST requests](#post-requests)
  - [Add new Highscore](#add-new-highscore)
- [DELETE Requests](#delete-requests)
  - [Potential Feature: Delete All Highscores](#potential-feature-delete-all-highscores)

## Server type

We employ Jetty as our HTTP server to host the REST API. This means that Jetty is the server workhorse, responsible for both processing the HTTP requests, and delivering the appropriate HTTP-responses. Our REST service specifically manages the `/highscores` endpoint, handling all highscore-related operations within our application.

## GET requests

### Retrieve all highscores

- **Description:** Fetches the complete list of highscores recorded by the application.
- **Endpoint:** `GET http://localhost:8080/highscores`
- **Parameters:** None
- **Response:**
  - `200 OK`: Returns a JSON-formatted list of `UserScore` objects.

#### Example of response body

```json
[
  {
    "name": "Player1",
    "score": 10,
    "date": "2023-01-24",
    "difficulty": "EASY"
  },
  {
    "name": "BobTheMiner",
    "score": 16,
    "date": "2001-09-11",
    "difficulty": "HARD"
  },
  {
    "name": "George Mallory",
    "score": 35,
    "date": "1924-06-08",
    "difficulty": "MEDIUM"
  }
]
```

## POST requests

### Add new Highscore

- **Description:** Submits a new highscore to be added to the server's highscore list.
- **Endpoint:** `POST http://localhost:8080/highscores`
- **Parameters:**
  - `Content type`: `application/json`
  - `Request body`: JSON-formatted `UserScore` object.
- **Response:**
  - `200 OK`: The highscore was successfully added.

Example of request body:

```json
{
  "name": "MyNewScore",
  "score": 178,
  "date": "2023-07-31",
  "difficulty": "HARD"
}
```

## DELETE Requests

### Potential Feature: Delete All Highscores

- **Description:** Deletes all highscores.
- **Endpoint:** `DELETE http://localhost:8080/highscores` (Not active)
- **Parameters:** None
- **Response:**
  - `200 OK`: Indicative of a future scenario where all highscores will be successfully deleted upon request.
  - `501 Not Implemented`: Currently returned by the server to reflect the absence of this functionality.
