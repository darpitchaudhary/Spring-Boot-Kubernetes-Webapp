# Spring-Boot-Kubernetes-Webapp
Containerized Spring Boot microservices to be deployed on Kubernetes Cluster implementing
- Webapp Microservice: Implementing User CRUD operations and User based Alerts
- CronJobs: Periodic Kubernetes Cron Jobs Producers calling HackerNoon Api to gather story Ids and publishing them on Kafka Topics
- Processor Webapp Microservice: Implementing Kafka consumers which poll Kafka Topics for available story ids 
- Notifier Webapp Microservice: Periodically Poll for Story Titles matching User based alert and sending AWS SES enabled email when a story matches

