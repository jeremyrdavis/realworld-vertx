# realworld-vertx

This project is a Vert.x implementation of the Thinkster Realworld project.

## Building

Right now there is only a Maven build:

```
mvn clean install
```

## Running

Running the associated Postman tests requires a MongoDB instance to be running.

The Docker command to start MongoDB is: 

```
docker run -d -p 27017:27017 mongo 
```

To load the data in the /data directory run (from the /data directory):

```
mongoimport --db conduit --collection conduit_users --file users.json --jsonArray
```