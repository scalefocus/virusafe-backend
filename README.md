# ViruSafe Backend

ViruSafe aims to help the fight with COVID-19 by offering people to share their symptoms as well track the spread of COVID-19 with an interactive map, that shows how the infection has spread throughout Bulgaria.

The ViruSafe mobile app provides access to the following:
- Receiving up-to-date information regarding COVID-19
- Regular sharing of symptoms
- Sharing your location, in order to compare your location to all users who have developed symptoms
- Option to be warned if you have been in close proximity to other symptomatic users
- Option to receive location-based notifications and alerts

Overview:
- [ViruSafe Backend](#virusafe-backend)
  - [Requirements](#requirements)
  - [Environment Setup](#environment-setup)
    - [Using docker-compose](#using-docker-compose)
    - [Manual install - on local machine](#manual-install---on-local-machine)
  - [Configuration](#configuration)
  - [Build Instructions](#build-instructions)
  - [Code Styleguide](#code-styleguide)
    - [Formatting](#formatting)
    - [Test Coverage](#test-coverage)
  - [Run the application](#run-the-application)
    - [It is a standard Spring Boot 2.2.X application](#it-is-a-standard-spring-boot-22x-application)
    - [Validate that everything is up and running localhost:8080/actuator/health](#validate-that-everything-is-up-and-running-localhost8080actuatorhealth)
  - [REST API authentication workflow documentation](#rest-api-authentication-workflow-documentation)
  - [REST API Swagger documentation](#rest-api-swagger-documentation)
  - [Contributing](#contributing)
  - [Security](#security)
  - [Contacts](#contacts)
  - [License](#license)

## Requirements

  - Java 11 or newer
  - MariaDB
  - Elasticsearch
  - ***[Optional]*** Kafka

## Environment Setup
  ### Using docker-compose

  1. Install docker-compose https://docs.docker.com/compose/install/
	
  2. Run MariaDB, Elasticsearch, Kibana and DB Adminer using docker/docker-compose.yml
		
      Run:
        ```
        docker-compose -f .\docker\docker-compose.yml up
        ```
		
      Configuration data:
		
      1. MariaDB
          ```
          url: jdbc:mariadb://localhost:3306/virusafedb
          username: virusafe
          password: dev
          ```

			Run SQL scripts to initialize DB in the order:
   			- src\main\resources\db\ddl\virusafe_ddl.sql
   			- all scripts in name order from directory src\main\resources\db\ddl\v1
				
			You may use your preferred SQL client or Adminer http://localhost:12398/adminer.php (official site https://www.adminer.org/en/editor/) part of docker-compose file
			
          In Adminer for server you should use container name mariadb:3306 to access internal DB
			
		2. Elasticsearch:
            ``` 
            url: localhost:9200
            ```
			
			You may use Kibana to administrate it http://localhost:5601

     3. ***[Optional]*** running Kafka - docker/docker-compose-kafka.yml
		  Run: 
        ```
        docker-compose -f .\docker\docker-compose-kafka.yml up
        ```

		  Kafka broker will run on: localhost:9092
		
		  You may use Kafdrop to check Kafka single node cluster using http://localhost:19000/

  ### Manual install - on local machine
  1. Install MariaDB - https://mariadb.com/kb/en/getting-installing-and-upgrading-mariadb/
   
      1. Create user and database for ViruSafe
  
      2. Run SQL scripts to initialize DB
			- src\main\resources\db\ddl\virusafe_ddl.sql
			- all scripts in name order from directory src\main\resources\db\ddl\v1
		
  2. Install Elasticsearch - https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html
	
  3. ***[Optinal]*** Install Kafka - https://kafka.apache.org/quickstart
	
  4. Configure everything in src/main/resources/application.properties

## Configuration

***Note:*** If you use docker-compose environment everything should be pre-configured
1. ### Configure MariaDB
    ```
    spring.datasource.url=jdbc:mariadb://localhost:3306/virusafedb
    spring.datasource.username=virusafe
    spring.datasource.password=dev
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
    ```
	
2. ### Configure Elasticsearch
    ```
    spring.elasticsearch.rest.uris=localhost:9200
    spring.elasticsearch.rest.username=
    spring.elasticsearch.rest.password=
    ```
	
3. ### ***[Optinal]*** Configure Kafka
    ```
    spring.kafka.bootstrapServers=localhost:9092
    
    #switch on Kafka AutoConfiguration - you should comment, as it is bellow (# sing in front), or remove next line
    #spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
    
    #you should enable Kafka integration - see next line
    register.integration.kafka.enabled=true
    ```

## Build Instructions

```
./mvnw clean install
```
  
## Code Styleguide
### Formatting
We use our own `backend-code-formatter.xml` file, which can be found in the repo.

### Test Coverage
We set a target of 80% code coverage, it is enforced by maven build using jacoco plugin.

## Run the application
  ### It is a standard Spring Boot 2.2.X application
  1. #### In IDE - run VirusafeApplication
  2. #### Using maven

      ```
      ./mvnw spring-boot:run
      ```

  3. #### In console
    
      ```
      java -jar target/viru-safe-0.0.1-SNAPSHOT.jar
      ```
  ### Validate that everything is up and running [localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## REST API authentication workflow documentation
-  Authentication in the system is based on JSON web tokens (JWT). In order to generate a pair of access and refresh tokens you should execute the following flow: 
    - call the /pin endpoint with clientId as a required header(you can get it from `application.properties` file under jwt.client-ids property). For development purposes, LogOnlySMSProvider is used and this pin code comes from `application.properties` file under pin.generation.strategy property.
    - the next step is to call the /token endpoint that uses the pin from the previous call and generates both access and refresh tokens.
- With calling these two endpoints and receiving the tokens we can approach any of the other endpoints.
## REST API Swagger documentation
- you may use `swagger.yaml` in base directory
- the actual version should be accessible on running application at [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## Security

If you happen to find a security vulnerability, we would appreciate you letting us know by contacting us on - virusafe.support (at) scalefocus.com and allowing us to respond before disclosing the issue publicly.

## Contacts

Feel free to checkout our [Slack Team](https://join.slack.com/t/virusafe/shared_invite/zt-dthph60w-KGyk_s6rjoGa6WjR7~tCAg) and join the discussion there ✌️

## License

Copyright 2020 SCALE FOCUS AD

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
