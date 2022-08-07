## Interface Project (Interface Definition Language- IDL)

![](https://yidongnan.github.io/grpc-spring-boot-starter/assets/images/server-project-setup.svg)

We recommend splitting your project into 2-3 separate modules.

1. **The interface project** Contains the raw protobuf files and generates the java model and service classes. You probably share this part.
2. **The server project** Contains the actual implementation of your project and uses the interface project as dependency.
3. **The client projects** (optional and possibly many) Any client projects that use the pre-generated stubs to access the server.


## [Tutorials](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html#interface-project)