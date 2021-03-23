##FROM java:8
FROM openjdk:12.0.2

ADD ./app/build/distributions/app-1.0.tar  /

WORKDIR /app-1.0/bin

## COPY ./*.pl ./
## RUN ls

CMD ["bash", "app"]


## docker build -t vrobotclient:1.0 .
## docker run  -ti --rm vrobotclient:1.0 /bin/bash