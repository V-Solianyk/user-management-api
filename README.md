
<h1 style="font-size: 42px;">UserManagementAPI</h1>

# Summary
UserManagementAPI is a simple REST application that allows you to create users and register only those who pass certain age restrictions, as well as update user data, delete, and receive all users in a certain age range.
# Endpoints
<h1 style="font-size: 14px;">Acceptable endpoints in the application:</h1>

- POST: /users - create a new user.
- PUT: /users/{id} - update the user by id.
- DEL: /users{id} - delete the user by id.
- GET: /users{id} - get the user by id.
- GET: /users - get all users in the specified years of birth range.
- PATH: /users{id} - update one or some user fields.

# Project structure
- src/main/java: contains all the source code for the application.
- src/main/resources: contains configuration files and resources.
- checkstyle/checkstyle.xml - is a configuration file for the checkstyle tool, which is used to check the code style. It contains settings for various checkstyle modules that perform various code checks for compliance with style standards.
- pom.xml - used to configure and create a Maven project, add the necessary dependencies.

# Technologies used
- JDK 17
- SpringBoot 3.1.4
- H2 database
- Hibernate 6.2.9.Final
- Swagger 2.1.0
- Maven 4.0.0
- Mapstruct 1.4.2

# How to run the application
In order to launch this project, you need to take the following steps:
1. Clone this project from GitHub to your local machine.
2. Install the following software:
- IntelliJ IDEA (IDE) to run the application.
- Install Postman for sending requests, or you can use Swagger UI and you need follow this link to test the application - localhost:8080/swagger-ui.html
3. Open the project in IntelliJ IDEA.
4. Build the project using Maven: mvn clean package.
5. Once the configuration is complete, click the "Run" button in IntelliJ IDEA to start the application. You can choose either normal mode or debug mode.
7. If all the steps have been followed correctly, the server will start successfully.
7. Use Postman or a web browser to interact with the endpoints and test the application.
   Please follow these instructions carefully to launch the project.
