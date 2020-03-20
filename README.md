# "Eventhandling" demo application

## Preface
This project has been created due to a coding-contest at smec. It should be able to handle
account and event objects as well as statistics for each account per day per type. Events should be
deleted automatically after 30 days, but not vanish from the statistics. An estimated 100 accounts x 1000
events per account per day is expected (but probably extending to 1000 accounts x 10000 events
per account per day). No API security (or even any security) is required.

## Architecture
- `Java 8` (since this is used @smec afaik)
- `Gradle` dependency management and build tool
- `Spring boot` stack for the `REST Interface`
- `Spring data` for the repository layer
- `lombok` for reducing boilerplate code
- `JUnit 5 (Jupiter)` and `Mockito` for testing (+ `restdocs` and `asciidoc` for documentation)
- `mongodb` as persistence layer due to possible heavy loads (1000X10000) and expiration of
events after 30 days
- `docker` to provide the mongodb and ease the development process

## Running the application
### Development mode
Due to containerization of mongodb you can easily run the project using docker. For spinning up the containers
use the terminal of choice and `cd` to the root of the project. Spin up the mongodb via
`docker-compose up -d`. Afterwards you can run the project via your IDE of choice (eg. I recommend *IntelliJ*).

### Testing mode
To run all tests execute gradle *test* goal by running `./gradlew test` in the project root dir.

### Production mode
First you need to configure the new mongodb host in the `application.yml` file. If needed you
can extend the configuration by `username` and `password` for the mongodb host (this is not needed for development purposes due to using the default configuration).

You can create a runnable jar by executing `./gradlew clean build`, which creates a `*.jar` file in
`/build/libs/*.jar`. Now you have to host that jar wherever you want (eg. in a docker-compose
environment or even a kubernetes cluster).  

## Endpoints
Endpoint documentation is done by restdocs and asciidoc. To create the docs just run
`./gradlew clean test asciidoc`. Afterwards you need to run the application in your IDE, plug in
your headphones of choice, open [this link](https://www.youtube.com/watch?v=1VQ_3sBZEm0) and start reading the
endpoint documentation [provided with the application](http://localhost:8080/docs/index.html).
