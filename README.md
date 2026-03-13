# Shop API

![CI](https://github.com/Sayat86/shop-app/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-blue)
![Redis](https://img.shields.io/badge/Redis-cache-red)
![Prometheus](https://img.shields.io/badge/Monitoring-Prometheus-orange)
![Grafana](https://img.shields.io/badge/Visualization-Grafana-yellow)
![Docker](https://img.shields.io/badge/Docker-containerized-blue)

REST API for an online shop built with Spring Boot.

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* PostgreSQL
* Redis
* Flyway
* Prometheus
* Grafana
* Docker
* Swagger

## Features

* Product catalog
* User authentication
* Role-based authorization (USER / ADMIN)
* Pagination and filtering
* Redis caching
* Database migrations with Flyway
* Monitoring with Prometheus and Grafana
* REST API documentation with Swagger
* Containerized deployment with Docker

## Architecture

Client
↓
Spring Boot API
↓
PostgreSQL

Cache: Redis

Monitoring:
Prometheus → Grafana

## Running the project

### 1.Clone repository:

git clone https://github.com/Sayat86/shop-app

cd shop-app

### 2.Build the application:

mvn clean package

### 3.Run with Docker:

docker compose up --build

## Services

API
http://localhost:8080

Swagger
http://localhost:8080/swagger-ui.html

Prometheus
http://localhost:9090

Grafana
http://localhost:3000

## Monitoring

Application metrics are collected using Prometheus and visualized in Grafana.

Metrics include:

* HTTP request rate
* JVM memory usage
* CPU usage
* Error rate
