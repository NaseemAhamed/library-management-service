# Library management service 

-Private repository-

## Table of Contents
- Introduction
- Architecture
    - Technology stack
    - Sequence Diagram with project structure
    - Validation requirements
    - Assumptions
    - API Documentation
    - Project Structure
    - Performance Test Report
- Conclusion

------------------------------------------------------------------------

## Introduction

The objective of this project is to build a production-ready library management service. 
The user can view, borrow and return books. 

## Technology Stack

- **Spring WebFlux**
    - Fully asynchronous and non-blocking stack end-to-end including database interaction with reactive MongoDB.
    - Handles concurrency with less resources and scales well.
    - Prominent performance improvements. Service-to-service communication by WebClient is statistically faster than the conventional ones. The difference becomes more noticeable on high concurrent uses.
    - Functional programming -> More declarative code, especially with the usage of Java 8+ APIs.
    - Support for testing with StepVerifier and WebTestClient.

- **Reactive MongoDB**
    - Favours non-blocking db communication well.
    - Auto-sharding.
    - It provides MongoDB Compass, a matured and intuitive GUI for backend data visualisation and manipulation.
    - Performance is relatively higher.
    - Easy to scale.

- **JUnit, Mockito and reactor-test**
    - Unit tests in this application use Jupiter JUnit APIs and Mockito features.
    - Component tests use WebTestClient to test the service routers.
    - StepVerifier is used for testing methods wherever a publisher is returned.
    
- **Slf4j**
    - Audit Trail
    
- **Java >= 8**
    - Language and platform
    
------------------------------------------------------------------------

## Business logic and assumptions

### Sequence Diagram with project structure
[SequenceDiagram.png] (https://github.com/NaseemAhamed/library-management-service/blob/master/SequenceDiagram.png)

- The above diagram available at the *root directory* elucidates the business logic and the implemented code flow. 

### Validation requirements
- Each user has a borrowing limit of 2 books
- Only 1 copy of a book can be borrowed by the user

### Assumptions
- The user who has borrowed the book can only return the book.
- The available book information from library are _id, bookName and authorName. 
- Id of database record is taken as ISBN.
- As the user selects from the already available list of books while borrowing and returning, both _id and bookName together are used for lookup references.
- If one book is returned and another book is not, the case is treated as valid and handled appropriately.
- Authentication is not required.

### API Documentation

[PostMan Collection] (https://github.com/NaseemAhamed/library-management-service/blob/master/library-management-service.postman_collection.json) 

(PostMan Collection is available in the root directory)


**1. User can view books in library**

Request:

```
GET /v1/books
```

Body:

``
Not Applicable
``

Header:

``
Not Required
``

Response:

```json
[
    {
        "id": "5f137aef625ec410888de04e",
        "bookName": "Legend of Zelda",
        "authorName": "Koji"
    }
]
```
------------------------------------------------------------------------
**2. User can borrow a book from the library**

Request:

```
POST /v1/books/user1?action=borrow
```

Body:

```json
{
    "id": "5f137aef625ec410888de04e",
    "bookName": "Legend of Zelda"
}
```

Header:

```
Accept: application/stream+json
Content-Type: application/stream+json
```

Response:

Book with Id 5f137aef625ec410888de04e is borrowed.

------------------------------------------------------------------------

**3. User can borrow a copy of a book from the library**

Same as API Number 2. The validation to borrow only one copy of a book is done.

------------------------------------------------------------------------

**4. User can return books to the library**

Request:

```
POST /v1/books/user1?action=return
```

Body:

```json
{
  "books": [
    {
        "id": "5f137aef625ec410888de04e",
        "bookName": "Legend of Zelda"
    }
  ]
}
```

Header:

```
Accept: application/stream+json
Content-Type: application/stream+json
```

Response:
Book(s) with the following ID are returned: 5f137aef625ec410888de04e

------------------------------------------------------------------------

### Execution:

- To boot the application: 
    i. mvn clean install
    ii. mvn spring-boot:run

- To run all tests:
    - mvn test
    
- To run all component tests:
    - mvn test -Dgroups=component
   
    
### Note:

The database is hosted in cloud and its configuration is available in src/main/resources/application.properties. So, the application can be started with ease. 

In order to populate library with some books for functional testing, execute ViewBooksTest.java. 

Each component test resets the required database states before its execution as how it should be. 

    
- To execute the application locally, 
    - Run Application.java in default profile.

- Test specs:  
    - DTO Validator tests - com/library/management/common/BeanValidationTest.java.
    - User Story Component tests - com/library/management/component/test/*
       - Order:
            - User Story 1 :  ViewBooksTest.java
            - User Story 2 :  BorrowBooksTest.java
            - User Story 3 :  BorrowCopyBooksTest.java
            - User Story 4 :  ReturnBooksTest.java
            
------------------------------------------------------------------------

### Performance Test Report

The following read performance test is done on my system with a short ramp-up period of 20 seconds.
Throughput obtained was 87.7/sec. 

The report and aggregate graph can be found at the following link
[performance test report]

------------------------------------------------------------------------

## Conclusion

The service can be further enhanced with AuditEventRepository and trace ID to visualise the history of transactions.
For production environment, CircuitBreaker can be put in place at handler level and environment-specific configuration can be maintained in a centralised repository. As such, the basic 12 factor apps agreements are supposed to be in place.
The implemented pattern was aimed at reaping the benefits of ACID and had heavily utilised Project Reactor for better resource utilisation. Any other reactive NoSQL databases like Couchbase can also be easily plugged in this architecture if MongoDB requires replacement.

Please contact me with pingnaseem@gmail.com if you face issues executing the project.



    
    
