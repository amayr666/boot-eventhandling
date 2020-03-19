# "Eventhandling" demo application

## Preface
This project has been created due to a coding-contest of smec. It should be able to handle
account and event objects as well as statistics for each account/day/type. Events should be
deleted after 30 days, but not vanish from the statistics. An estimated 100 accounts x 1000
events per account per day is expected (but probably extending to 1000 accounts x 10000 events
per account per day). No API security (or even any security) is required.

## Architecture
- Java 8` (since this is used @smec afaik)
- `Gradle` dependency management and build tool
- `Spring boot` stack for the `REST Interface`
- `Spring data` for the repository layer
- `lombok` for polishing java code`
- `JUnit 5 (Jupiter)` and `Mockito` for testing (+ `restdocs` and `asciidoc` for documentation)
- `mongodb` as persistence layer due to possible heavy loads (1000X10000) and expiration of
events after 30 days _(this is my first use of mongodb - please be kind)_
- `docker` to provide the mongodb and ease the development process

## Running the application
### Development mode
Due to containerization of mongodb you can easily run the project. For spinning up the containers
use the terminal of choice and cd to the root of the project. Spin up the mongodb via
`docker-compose up -d`. Afterwards you can run the project via your IDE of choice.

### Testing mode
To run all tests execute gradle test goal by `./gradlew test` in the project root dir.

### Production mode
First you need to configure the new mongodb host in the `application.yml` file. If needed you
can extend the config by username and password (this is not needed in default configuration).

You can create a runnable jar by `./gradlew clean build`, which creates a `*.jar` file in
`/build/libs/*.jar`. Now you have to host that jar wherever you want (eg. in a docker-compose
environment).  

## Endpoints
Endpoint documentation is done by restdocs and asciidoc. To genereate these run
`./gradlew clean test asciidoc`. Afterwards you need to run the application in your IDE, plug in
your headphones of choice, open [https://www.youtube.com/watch?v=1VQ_3sBZEm0](this link) and start reading the
endpoint documentation [http://localhost:8080/docs/index.html](provided with the application).
