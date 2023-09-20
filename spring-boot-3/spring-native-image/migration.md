## Tutorials
- [Migrating an Application to Native Image With Spring Boot 3](https://medium.com/better-programming/experience-in-migrating-an-application-to-native-image-with-spring-boot-3-422d15efa31)

## [Why you should upgrade your services?](https://www.moderne.io/blog/speed-your-spring-boot-3-0-migration)

It’s easy to take an approach of “if it’s not broken, why fix it?” However, support for Spring Boot 2.x will end at the end of 2023. This means you’ll need to make all of these upgrades by then to ensure that your software remains secure when new CVEs are published. Furthermore, by upgrading, you’ll get access to a host of new features across many tools.

For example, by upgrading to Java 17, which is a requirement for Spring Boot 3.0, you’ll not only get a wide variety of new Java language features (records, pattern matching, switch expressions, etc.), but you’ll also benefit from performance improvements made to the virtual machine and garbage collector.

Note: Your environment will have a unique performance footprint. It is a good idea to use a monitoring platform to create a baseline of your applications' performance today, so you can quantify the savings when you upgrade.

In regards to Spring Boot 3.0, you can benefit from better support for building native executables and using the GraalVM. By building your Spring applications as native executables, you’ll find significant improvements in startup time. You’ll also find that observability has been a key theme in this new version, with tracing now being implemented via Micrometer Tracing. 

## [What you’ll need to update when migrating to Spring Boot 3.0](https://www.moderne.io/blog/speed-your-spring-boot-3-0-migration)

Moving to Spring Boot 3.0 includes a number of associated migrations and dependency updates that you must do prior to migrating to this new Spring Boot version, including:

- Upgrade your organization’s applications, infrastructure, and CI/CD pipeline to use Java 17. The good news with this step is that this work can be performed prior to upgrading any of your Spring Boot applications.
- 
- Any of your existing Spring applications that leverage Java EE will require an update to Jakarta EE 9. This may seem like a straightforward exercise that involves moving all imports from the `javax` namespace to the `jakarta` namespace but this also requires that any third-party libraries also be migrated to versions that are compatible with Jakarta EE 9.
- 
- Finally, depending on which version of Spring Boot your applications are being migrated from, there may be several required changes to both the application’s code and configuration when moving to Spring Boot 3.0