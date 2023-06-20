# Dockerized

- [Microservices: Quarkus vs. Spring Boot](https://www.linkedin.com/pulse/microservices-quarkus-vs-spring-boot-ualter-otoni-azambuja-junior/)

## Spring Boot

```shell
# Using Buildpacks
# https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.custom-image-name

spring$ ./gradlew bootBuildImage --imageName=$${IMAGE_NAME}
spring$ docker run -i --name spring --rm -p 8082:8082 spring/spring
```

## Quarkus

See `quarkus/src/main/dcoker/Dockerfile.native`

```shell
quarkus$ ./gradlew build -Dquarkus.package.type=native
quarkus$ docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus .
quarkus$ docker run -i --rm -p 8080:8080 quarkus/quarkus
```

Alternative (currently not working)

```shell
# https://quarkus.io/guides/container-image#buildpack

quarkus$ ./gradlew addExtension --extensions='container-image-buildpack'
quarkus$ ./gradlew build -Dquarkus.container-image.build=true
```

![image](https://github.com/logbasex/coding-notes/assets/22516811/1949ac7d-1f45-4d9a-a868-069ec889f186)