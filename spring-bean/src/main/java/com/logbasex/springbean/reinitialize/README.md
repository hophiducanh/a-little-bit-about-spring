## [Reinitialize Singleton Bean in Spring Context](https://www.baeldung.com/spring-reinitialize-singleton-bean)
Steps:

1. Hitting the URL http://localhost:8080/config/property1 returns `value1`.
2. We'll then change the value of property1 from value1 to `value2`.
3. We can then hit the URL http://localhost:8080/config/reinitializeConfig to reinitialize the config map.
4. If we hit the URL http://localhost:8080/config/property1 again, we'll find that the value returned is `value2` (Put your eye on `.resource` caching error).

## [Dynamically Changing Spring Bean Instances](https://reflectoring.io/spring-bean-lifecycle/)

Về cơ bản, theo mình đây là trường hợp ít khi cần dùng trong thực tế. Vì bạn đã có database để làm những việc như thế. Có chăng là trường hợp cần thay đổi database connection bean thì cần expose API gọi vào service `implements BeanFactoryAware` chẳng hạn để update lại.

Nếu không expose API thì có thể update lại resource file rồi reinitialize bean nhưng cách làm này không phổ biến và có vẻ không hiệu quả.