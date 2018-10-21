Magneato is a Content Management System (CMS) that has faceted navigation and structured pages at its core.
It was developed using the Dropwizard framework and Elastic Search

Open Source and written in Java. Page templates use Alpaca Forms. Data is stored as JSON that can be imported and exported to other CMS.
Optimized read path, intelligent multilevel caching, Java JVM and a blistering NoSQL data store means that Magneato can serve lots of pages, fast.
Java core, a flexible Unix-like Security Model as well as multi-layered registration, page creation and comment authentication keeps your data secure.
Technology

    Based Dropwizard v 1.3.4
    Elastic for search / facetting / storage
    Alpaca for page structure
    NoSQL

Changes in Version 3.0

Orbeon Forms has been replaced with Alpaca. Spring has been dropped for Dropwizard


How to start the Magneato application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/Magneato-3.0-SNAPSHOT.jar server config.yml` - with a Java 8 JVM
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
