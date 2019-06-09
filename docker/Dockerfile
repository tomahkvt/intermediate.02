FROM maven:3-jdk-8


RUN apt-get update
RUN apt-get install -y netcat


RUN mkdir -p /app
ADD src /app/src
ADD pom.xml /app/pom.xml
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip=true
WORKDIR /app/target
CMD java -jar com.task.springboot-1.0.0.jar