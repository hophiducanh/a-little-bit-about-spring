## [Tutorials](https://viblo.asia/p/grpc-grpc-vs-rest-performance-oOVlYv1458W)

1. Create Gradle module: `grpc-benchmark`
2. Clean all files in this module
3. Create sub-modules.

Performance benchmark using [Apache Bench](https://www.datadoghq.com/blog/apachebench/) (Already available in Ubuntu 20.04)

```shell
apt-get update
apt-get install -y apache2-utils


//grpc
ab -n 1000 -c 100 http://localhost:8181/grpc/unary/1000

//rest
ab -n 1000 -c 100 http://localhost:8181/rest/unary/1000
```