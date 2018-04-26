# centralizedconfigservices

With microservices, we create a central config server where all configurable parameters of micro-services are written version controlled. Config server is where all configurable parameters of microservices are written and maintained. It is more like externalizing properties / resource file out of project codebase to an external service altogether, so that any changes to that property does not necessitate the deployment of service which is using the property. All such property changes will be reflected without redeploying the microservice.

------------------------------------------------------------------------------------------------------------------------------
Creating config-server :

Go to https://start.spring.io/, then create a template project using the dependency Config server.
Import the project in STS 
Open the Spring Application class under src/main/java package 
Add @EnableConfigServer annotation on top of the class. By doing this we tell the Spring Boot app to treat it as a config server module	

	src/main/java

	
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.cloud.config.server.EnableConfigServer;

	@EnableConfigServer
	@SpringBootApplication
	public class ConfigServiceApplication {
	    public static void main(String[] args) {        										SpringApplication.run(ConfigServiceApplication.class, args);
	    }
	}

Add the following in the application.properties 
src/main/resources/application.properties
server.port=8888 //port the server will be running on
spring.cloud.config.server.git.uri=https://github.com/prajna1680/centralizedconfigservices.git //Git repo location 

----------------------------------------------------------------------------------------------------------------------------
Creating Git Repository :

Create a new directory in your file system.
Create a file config-server-client.properties file in your directory and add the message there.
Open command prompt from your directory and run command git init to make that directory as git repository.
Run git add . to add everything to this repo.
Finally we need to commit the properties file by running command git commit –m "initial checkin". This should check in all the files in the git repository.

	config-server-client.properties
	message=Hello world from config server

----------------------------------------------------------------------------------------------------------------------------
Creating config-client:

Go to https://start.spring.io/and generate client project with dependencies actuator, web, config-client & import it into STS
Actuator: If any properties for a microservice have been changed, that means they have been refreshed through an Actuator refresh REST endpoint. By doing this, our microservice got updated without rebuilding the application.
Create a file called bootstrap.properties in the src\main\resources directory and add the below properties to connect with the config server along with some required configuration.
src/main/resources/bootstrap.properties
spring.application.name=config-server-client //application name of the microservice that would be deployed.
	spring.profiles.active=development //Active Profile - will relate to development 	properties file in the server. If this property is absent then default profile will be 	activated which is the property file without any environment name at the end.
	spring.cloud.config.uri=http://localhost:8888 //property to mention the config server 	url
We also want to enable the /refresh endpoint so that we can demonstrate dynamic configuration changes:
 /src/main/resources/application.properties
management.endpoints.web.exposure.include=*

Open the Spring Application class under src//main/java package
Create a Spring REST controller that returns the resolved message property’s value. 
By default, the configuration values are read on the client’s startup, and not again. You can force a bean to refresh its configuration - to pull updated values from the Config Server - by annotating the MessageRestController with the Spring Cloud Config @RefreshScope and then by triggering a  refresh event. 
	src/main/java
	
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	mport org.springframework.cloud.context.config.annotation.RefreshScope;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RestController;
	
	@SpringBootApplication
	public class ConfigClientApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(ConfigClientApplication.class, args);
	    }
	}
	@RefreshScope
	@RestController
	class MessageRestController {
	    @Value("${message:Hello default}")
	    private String message;
	    @RequestMapping("/message")
	    String getMessage() {
	        return this.message;
	    }
	}

----------------------------------------------------------------------------------------------------------------------------
Run & Test the application

Build and Run Config Server Project :
Open command prompt from server folder and run mvn clean install followed by mvn spring-boot:run
This will start the config server service in 8888 port in localhost.

Build and Run Config Client Project :
Open command prompt from client folder and run mvn clean install followed by mvn spring-boot:run
This will start the Config Client service in 7777 port of localhost.

Test REST Endpoint:
open the /message rest endpoint by browsing the url http://localhost:7777/message
return Hello default - this is from config server

Test Property Change:
Change the message key from configuration file in the Git repository to something different.
You can confirm that the Config Server sees the change by visiting http://localhost:8888/a-bootiful-client/default
You need to invoke the refresh Spring Boot Actuator endpoint in order to force the client to refresh itself and draw the new value 
Invoke the refresh Actuator endpoint by sending an empty HTTP POST to the client’s refresh endpoint, http://localhost:7777/actuator/refresh(use postman)
Confirm it worked by reviewing the http://localhost:7777/message endpoint.
