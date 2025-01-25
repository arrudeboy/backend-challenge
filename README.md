# backend-challenge

> This challenge is about 'microservices' architecture and the following diagram describes the interaction between the underlying components:

![img](https://i.imgur.com/egbhzYG.png)

## Requirements

### functional requirements

 - **_Perform a calc with a dynamic percentage_**: given two numbers (`num1` and `num2`) sum both and then apply an addition with a percentage of this sum result. Example: `num1=5`, `num2=5`, `percentage=10%` => **`(5 + 5) + 10% = 11`**.

    > The percentage value must be retrieved from an external service (might be a mocked service)

 - **_Save all client calls data for future querying_**: each client call entry must save the following information:
   - `Date and time`
   - `Invoked endpoint`
   - `Received parameters`
   - `Response (either successful or failure)`
   <br></br>
   > The call history querying must support pagination
### non-functional requirements

 - **_Cache_**: store the percentage value with a **`ttl`** of **30 minutes**
 - **_Fallback_**: if the external service fails by retrieving the percentage then return the last stored value in the cache
 - **_Retry_**: it must retry at least **3** times to retrieve a successful response from external service before fails or return the cached value
 - **_Asynchronous saving_**: it must record the client call data in an asynchronous way without blocking the main client request thread
 - **_Rate limiting_**: max request per minute must be **3**. If a client reaches this threshold, return a response accordingly (e.g `"429 too many requests"`)

### constraints

The REST API and subtenant components must be developed by using the following technologies:
 - Programming language: `Java 21`
 - Database: `Postgres`
 - Containerization: `Docker`
 - API doc: `Swagger`

## Deploying and Testing the solution (HOW-TO)

### prerequisites

All components regarding this solution are deployed inside isolated containers. The containerization engine used is `Docker`
1. __Docker__ (version 27.5.1 or newer):

    * If you have Docker installed, check its version by typing the following in a command line:

        ```
        docker --version
        ```

2. __Docker Compose__ (version 2.32.4 or newer)

    * If you have Docker Compose installed, check its version by typing the following in a command line:

        ```
        docker compose version
        ```

### deployment

On the project main directory run the following command to deploy all the containers:
```
docker compose up -d
```

Check that all the containers have been deployed correctly by running the following command:
```
docker ps
```

> exemplary expected output:
> 
> ![img](https://i.imgur.com/PNyWnvn.png)

### testing

#### postman :rocket:

Import this [postman collection](tenpo-backend-challenge.postman_collection.json) in order to start testing the REST API endpoints.
There are also extra requests in this collection to interact with the percentage-external-service

#### swagger 

> :exclamation: :collision: Unexpected behaviour is taking place with spring boot 3 and swagger, thus it leads the fact that the documentation cannot be displayed at common urls (http://localhost:8080/swagger-ui.html or http://localhost:8080/swagger-ui/index.html). A 404 response is returned when trying access these urls.

## final thoughts

The challenge was fine. Some requirements, specifically the technical ones were too generic and vague. I decided not to use Webflux, and instead I opted to use Virtual Thread support on Spring Boot regarding the fact that it is a final feature in Java 21. In my opinion, reactive programming is not needed nowadays in mostly common scenarios and if you ask for performance, high-conconcurrency and scalability in Java, might be it's time to delve into [Project Loom](https://openjdk.org/projects/loom/).

Speaking about distributed cache with Redis, I did it in the past in other projects by using common libraries like Jedis, but due to lack of time I could not implement it. Anyways, I consider that an unnecessary feature regards the challenge scope.
Going to a kubernetes environment is another story, and again I think that it is out the scope of this challenge.
