# **Get-Together - Server**

<img src=https://github.com/sopra-fs24-group-11/client/blob/main/src/graphics/Get-Together.png />

## Table of Content

- [Introduction](#introduction)
- [Technologies Used](#technologies-used)
- [Main Components](#main-components)
- [Launch & Deployment](#launch--deployment)
- [Roadmap](#roadmap)
- [Authors and Acknowledgment](#authors-and-acknowledgment)
- [License](#license)

## Introduction
Welcome to Get-Together! Our platform revolutionizes trip planning by providing users with intuitive tools to organize, share, and manage their journeys seamlessly. Whether you're coordinating a weekend getaway or a cross-country adventure, Get-Together enhances every step of the planning process, making it both efficient and enjoyable.


[Start planning your trip now!](http://sopra-fs24-group-11-client.oa.r.appspot.com/)


## Technologies Used
* [Spring](https://spring.io/projects/spring-framework) - Framework that enables running JVM and JPA
* [Gradle](https://gradle.org/) - Build automation tool
* [Transport OpenData](https://transport.opendata.ch/docs.html) - External API



## Main Components

The [controllers](https://github.com/sopra-fs24-group-11/server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller) act as the receivers of REST calls. They handle incoming requests and delegate the necessary tasks to the appropriate services. We chose to implement two such controllers, namely the TripController and UserController, so that a functional seperation can be achieved between trip logic and pure user logic (like profile management).<br />
The services play a crucial role in the project as they encapsulate the core logic and ensure the integrity of our trip planner. These services are responsible for creating trips, adding & inviting friends, joining and leaving trips as well as handling list actions.<br />
Overall, the controllers and services form an integral part of the Get-Together project. The controllers handle incoming requests while the services ensure proper user & trip logic and functionality.


## Launch & Deployment
### Prerequisites
All dependencies are handled with Gradle. <br />
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java). \

### Clone Repository
Clone the client-repository onto your local machine with the help of [Git](https://git-scm.com/downloads).

```bash 
git clone https://github.com/sopra-fs24-group-11/server.git
```
You can find the corresponding client repository [here](https://github.com/sopra-fs24-group-11/client).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```
You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```
We also recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

### Deployment
The main branch is automatically mirrored onto Google Cloud App Engine via GitHub workflow, each time you push onto the main branch. 


## Roadmap
Potentially interesting additions to our project could be:
- Add secure connection (https instead of http).
- Adding car, bike and other transportation (not just limited on public transport).
- Make app responsive for mobile screens (since target audience is smartphone users).

## Authors and Acknowledgment

### Authors
* **Marc Heinimann** - [Marc-Hei](https://github.com/Marc-hei)
* **Gabriel Stegmaier** - [gstegm](https://github.com/gstegm)

### Acknowledgments
We  would like to thank our TA [Cédric](https://github.com/cedric-vr) and the whole team of the course Software Engineering Lab from the University of Zurich.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/sopra-fs24-group-11/server/blob/main/LICENSE) file for details.
