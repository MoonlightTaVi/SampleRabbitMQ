A set of code snippets and templates in a form of a sample Java Spring
project (+ RabbitMQ service).

The repository can be used in both educational purposes and as a reference
for code templates.

# Contents of the project

The project contains some boiler-plate templates for a number of use scenarios.

1. Java project / Docker structure:

- **Maven & Spring Boot compiler plugins (`pom.xml`)**: for a proper JAR building;
- **Dockerfile**: for the automatization of the build process;
- **Docker Compose file**: to start both the application and the other services 
inside the same container;
- **Maven wrapper (`mvnw`)**: for the off-line build mode;
- **Separate `.env` file**: shared between services inside the container;
- **Simple Logback configuration file**: to make a clean console output, but still keep track
of important warnings in several separate `.log` files;
- **Spring profiles**: separate `application.properties` for various goals (testing, development, production);
- _And some others_.

2. Java Spring code templates:

- **Simple RabbitMQ configuration for Spring**: can be easily adapted for specific cases;
- **Simple REST API**: includes a common shared mapping for end-points and JSON validation;
- **Simple Spring Shell command**: currently works in the script mode, 
serves as an interactive command line runner;
- **Jakarta/Spring bean validation with custom annotations**: 
for both the REST API and the Shell;
- _And some others_.

Comments are scattered across the code to highlight the important details.

# Running the application

The application can be started in two ways:

1. As an independent JAR:
- Build the JAR: `mvn -Dmaven.test.skip=true clean package` (does not include JUnit tests into the JAR to save memory);
- Or simply run `App.main()` as a Java application inside the IDE of the choice (Eclipse, IntellijIDEA, VSCode, NetBeans, etc.).

In this case, a RabbitMQ service must be started manually on the machine; its address and credentials must correspond to the ones inside the `.env` and `.properties` files of the application (or vice versa).

2. Inside the Docker container:
- Run `docker-compose up --build -d` inside the project root directory (in CMD/PowerShell/Bash) to start the container;
- Run `docker-compose down` to shut the container down (it also clears all the occupied resources).

The `--build` flag forces the Docker to re-compile the JAR image for the container each time (without it, the last compiled image will be re-used).

The `-d` flag starts the container in the `detached` mode (the shell will be able to accept new commands once the container is started; without this flag, Docker will occupy the shell input).

The second option (running in a container) is the **recommended** way to run
such services; Docker containers are isolated from the OS 
(it is similar to running in a "sandbox"). Docker Compose will also create
a new nested RabbitMQ image container for this application specifically (it will be automatically downloaded if needed).

> **Note**
>
> The `.env` file is not included into the project; it is a bad practice, even though the `.env.example` has absolutely the same contents. Rename `.env.example` to `.env` before running the application.
> 
> One of the advantages of such an approach is that anyone can put **real** credentials into the `.env` file, but they will never get leaked (since it is already ignored by `git`).

# What does the application do

By default, nothing will happen upon the launch.

The application expects command-line agruments when it starts (this mechanism is powered by Spring Shell). For example:

`java -jar send demo.jar -s SenderName -r ReceiverName -a MoneyAmount`

Here:

- `-s` and `-r` represent the names of a person who sends the money and a person who must receive this money (both can be any kind of strings);
- `-a` represents the amount of the money transaction:
    - It must be any positive (strictly `> 0`) number;
    - It can be either integer (like: `100`) or decimal (like: `100.00`); 
    - There must not be more than 2 digits after the period sign (if we consider that the used currency is "dollars", than the minimal step is "1 cent" or "$0.01").

When running via `docker-compose`, the command may be changed inside the `Dockerfile` (it already contains a sample command).

When running from IDE, the arguments may be specified in the "run configuration" settings of the project (it is IDE-specific and must be set manually).

Then, the application will imitate the money transaction:

- Spring Shell will make an `HTTP POST` request to the server (with the server being the same application);
- REST controller will send a message to the RabbitMQ service;
- Rabbit listener inside the application will accept this message and log it to the file and to the console.

Each step in this algorithm is logged to the console in details. When running from a container, the console may be accessed through the Docker interface (either command line of GUI); the log files may be found inside the container, in the `/app` directory. P.S. Here, we are talking about the **application** sub-container inside the root container (it is usually called "app-1", because it doesn't have an explicitly specified name).

# Known issues

## Healthchecks

The Spring server starts faster than the RabbitMQ service. By default, it immediately makes a Rabbit message query, and it does not retry sending this message if the RabbitMQ service is not ready yet (unless the `retry` property is configured).

Therefore, the Docker Compose file specifies the `depends-on` property for the server; the container will wait until the RabbitMQ is "healthy" and only then it will start the server.

So, we have several problems here:

- Each service (e.g. PostgreSQL, RabbitMQ, Redis, etc.) has its own way of "healthchecking";
- Furthermore, each service may have several different ways to check its "health";
- What is even worse, **the same command for a healthcheck may _work_ or _not work_** in different cases.

Read the documentation/discussions regarding `docker-compose.yml` and the specific service to find all the possible commands for a healthckeck.

## Shell interactive mode

It is almost always a bad practice to run an interactive Shell and other Spring services (like a server or any other controller) in the same JAR. 
The logging and the shell output (while running on different threads) may conflict with each other, breaking the text in the console. It may be sometimes supressed by disabling logging to the console, but it is still not advised to do so.

Here, the Shell is used only for simplicity. It is fully functional (and may be `Ctrl+C/Ctrl+V`'d to another project without any issues), but the interactive mode is disabled in the `.properties` file.

## Logging

The application uses Logback; it is simple, it is sufficient, and it comes
in a bundle with any Spring Starter.

But the "real" applications mostly prefer using other loggers, such as
Log4j.

Besides, the Logback is configured to log in a single file for each separate package (3 files overall); the files are overridden on each application startup. The `RollingFileAppender` may be used to preserve the history of logs (which is unnecessary for this application).

## Timestamps

The time that is shown in logs / Rabbit messages does not take time regions
into account (because it is how it works). The used time region is Greenwich Mean Time, or GMT (UTC+0).

## Unhandled exceptions

The application does not handle most of the exceptions that may happen. For example, it will crash if someone tries to send negative amount of money (validation exception).

It is **not** because it is hard to implement; it is because this functionality is out of the application scope (e.g. the Shell component represents a separate web-client that makes requests to the remote server).

But the most crucial exceptions (such as the connection exception) are treated in the best way possible (as for a sample project).

# Example of the correct output

```
 :: Spring Boot ::                (v4.0.2)


[04:05:00.442] [main] INFO  [com.github.tavi.srmq.App]: Starting App using Java 17.0.18 with PID 1 (/app/demo.jar started by root in /app)

[04:05:00.451] [main] INFO  [com.github.tavi.srmq.App]: The following 1 profile is active: "dev"

[04:05:03.302] [main] INFO  [com.github.tavi.srmq.App]: Started App in 3.419 seconds (process running for 4.092)

[04:05:03.390] [main] INFO  [c.g.t.s.s.TransactionPublisherCLI]: Sending a new HTTP request.

[04:05:03.704] [http-nio-8081-exec-1] INFO  [c.g.t.s.controllers.TransactionsAPI]: Received message.

[04:05:03.718] [http-nio-8081-exec-1] INFO  [c.g.t.s.controllers.TransactionsAPI]: The message has been published.

[04:05:03.740] [org.springframework.amqp.rabbit.RabbitListenerEndpointContainer#0-1] INFO  [c.g.t.s.c.TransactionListener]: Message received: John has sent $100.0 to Jane at 23 Feb 2026, 04:05:03.

[04:05:03.764] [main] INFO  [c.g.t.s.s.TransactionPublisherCLI]: HTTP request success: true.
```