FROM openjdk:11-jre
RUN mkdir /app
COPY ./target/movies-0.0.1-SNAPSHOT.jar /app/movies-0.0.1-SNAPSHOT.jar
WORKDIR /app
CMD "java" "-jar" "movies-0.0.1-SNAPSHOT.jar"