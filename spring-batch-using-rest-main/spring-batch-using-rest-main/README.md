# Spring-Batch-REST
- Spring Batch and Spring REST example (Loading Data form CSV to Database using spring batch framework).
- Batch controlled and monitored through REST API's exposed (Starting, Aborting, Restarting and Monitoring).
- Resources loaded from Batch are exposed through Spring REST (Exposing Data loaded via spring batch for read and modifications).

## Requirements
For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven 3](https://maven.apache.org)
- Use `mvn --version` for verifying maven and `java --version` for verifying Java installations.

## Building and Running Application on Command Line

Clone from git repository `git clone URL`.
Navigate to project root, Open command Line and use `mvn clean install` enter. Alternativey use `mvn clean install -DskipTests=true` to skip tests.
Change Directory to `target` and use `java -jar library-0.0.1-SNAPSHOT.jar` enter to start the application.

## Running Application on IDE

Clone from git repository `git clone URL`.
File >> Import as `Existing Maven Project`.
Execute the `main` method in the `com.chaitanya.LibraryApplication` class from your IDE.
Application Built on STS IDE, can work on eclipse and Intellij IDE

## Note
- If `java.net.BindException: Address already in use` is caused while launching application modify `server.port` in `application.properties`
- Please add folder/directory path to save files through Upload API using `app.batch.file.landing-zone` property in `src/resources/application.properties`. Please make sure the Read/Write privilege rights are provided to the folder/directory path given 
- Access in-memory database `H2` by using `http://localhost:<configured_port>/h2-lib-console` url in your browser.
- Access Swagger UI using open-api using `http://localhost:<configured_port>/swagger-ui-lib.html` url in your browser.

## Given Problem Statement and Expectations

Functional Features:
-	CRUD interface to access the books by ISBN
-	Search API by any of the attributes of the books
-	Rest API to Import books from CSV (or any other format you prefer)
 
Technical Features:
-	DB Storage (any in memory approach will do)
-	Swagger on the REST API

Not Required:
-	User interface
-	API Authentication

## Assumptions
- Data in CSV is structured
- Skip Policy, Retry Policy and Restart Policy dosent corresponds to any business case for the given implmentation
- There colud be more...

## Basic Features Achieved
- API to Upload CSV file
- API to Start, Stop, Restart batch execution and Read Processing Information of current execution
- API to expose Resources which are loaded using Batch and manage (CRUD Operations) them.

## Pending
- Validation to allow only CSV file in file upload API `TO DO`
- TaskExecutor Integration to step; found some spring issue `JobExecutionDao Synchronization issue TO BE RESOLVED`
- Imporve Unit Test case coverage `TO DO`
- Integration with some `Observability` platform for monitoring runtime `TO DO`

## More help needed to get started?
- Sample Requests are available in `src/main/resources/requests.txt`.
- Sample CSV available in the same folder `src/main/resources/books.csv`.

## References & Books
- [Spring Documentation](https://docs.spring.io/spring-batch/docs/current/reference/html/job.html#configureJob)
- [baeldung.com](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)
- [Spring Batch In Action Book](https://livebook.manning.com/book/spring-batch-in-action/about-this-book/)
