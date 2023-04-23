# Chat Application 

### Web Socket

#### Some important notes:
- Authentication is being performed by auth0.


- It is necessary to change the value of app.auth.jwks-url: "your value". This value is found in Settings after creating the application in auth0. 


- Advanced Settings -> Endpoints -> Json Web Key Set.


- After identifying the user through auth0, it was possible to retrieve the token using the resource provided by auth0. This implementation will not appear in this repository because it is a repository intended only for the backend, but it is possible to find tutorials on the auth0 page itself.


## Important endpoints:

       http://localhost:8080/v1/ticket

       ws://localhost:8080/chat

- This endpoint will provide a valid ticket for the websocket connection.
It is necessary to send the token through Headers, with the value in the Authorization key. This ticket has a value of 60 seconds until it becomes invalid. If this happens, it is necessary to generate a new ticket.


- Request: Web Socket Request


- With the ticket in hand, just pass the ticket as a parameter in Params or in the url itself and request the connection.


    Example: ws://localhost:8080/chat?ticket=ab83f85b-cac2-4e66-80d7-ad2bd4f11b20


### docker:
docker compose up (up services)

docker build -t chat-app:1.0 .   (build image)


- Check if the ports for these services are not being used on your machine.

### links: 
- https://auth0.com

### Stack:  
- Java 17 
- SpringBoot 3.0.6
- Redis 
- Mongodb
