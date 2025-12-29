FROM openjdk:21
RUN groupadd -r app && useradd -r -g app -s /sbin/nologin app
USER app
COPY service/target/app.jar /home/app/app.jar
COPY bin /home/app/bin
WORKDIR /home/app
EXPOSE 8080:8080 5005:5005
CMD ["sh", "./bin/start.sh"]
