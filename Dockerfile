
# Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
# Click nbfs://nbhost/SystemFileSystem/Templates/Other/Dockerfile to edit this template

FROM alpine:latest

CMD ["/bin/sh"]

# Use an official Java runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory in the container to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

COPY javafx-base-20-win.jar /app
COPY java-fx-controls-20-win.jar /app
COPY javafx-graphics-20-win.jar /app
COPY javafx-swing-20.jar /app
COPY slf4j-api-1.7.32.jar /app
COPY slf4j-simple-1.7.32.jar /app
COPY webcam-capture-0.3.12.jar /app
COPY bridj-0.7.0.jar /app

# Make port 80 available to the world outside this container
EXPOSE 80

# Run the application when the container launches
CMD ["java", "-jar", "javafx-base-20-win.jar", "java-fx-controls-20-win,jar", "javafx-graphics-20-win.jar", "javafx-swing-20.jar", "slf4j-api-1.7.32.jar" ,"slf4j-simple-1.7.32.jar" ,"webcam-capture-0.3.12.jar", "bridj-0.7.0.jar"]

