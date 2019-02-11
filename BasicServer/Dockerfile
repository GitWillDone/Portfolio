FROM java:8
WORKDIR /Users/willdunn/GitWillDone/Computer_Programming/BasicServer
ADD target/BasicServer-1.0-SNAPSHOT-shaded.jar server.jar
ADD target/resources/ resources/
EXPOSE 8080
CMD java -jar server.jar