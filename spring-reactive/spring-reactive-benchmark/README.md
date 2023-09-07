## Courses
- https://medium.com/sysco-labs/reactive-programming-in-java-8d1f5c648012
- https://frandorado.github.io/spring/2019/06/26/spring-reactive-vs-non-reactive-performance.html
- https://techannotation.wordpress.com/2018/05/14/spring-reactive-performance-benchmark/


## Netty
- https://kkgulati.medium.com/avoid-reactor-freeze-reactive-programming-fdc0b4b5991
- https://stackoverflow.com/questions/48607114/how-to-set-event-loop-pool-size-in-spring-webflux-webclient

```
No, for performance testing between Reactive Spring (using Project Reactor) and Imperative Spring, you do not need to have the same thread pool sizes or settings.

When comparing the performance of Reactive Spring and Imperative Spring, you should consider factors like:

- The number of concurrent requests your application needs to handle.
- The nature of the I/O operations (blocking or non-blocking) within your application.
- The available hardware resources (CPU cores, memory).
- The expected latency and throughput requirements of your application.
```
## Benchmark

### Reactive
Env: Thinkpad X1 Intel® Core™ i7-8750H CPU @ 2.20GHz × 12, 46.5 GB RAM 

> ab -n 10000 -c 300 "http://localhost:7171/reactive/data"

```
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            7171

Document Path:          /reactive/data
Document Length:        66 bytes

Concurrency Level:      300
Time taken for tests:   37.838 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1370000 bytes
HTML transferred:       660000 bytes
Requests per second:    264.28 [#/sec] (mean)
Time per request:       1135.140 [ms] (mean)
Time per request:       3.784 [ms] (mean, across all concurrent requests)
Transfer rate:          35.36 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   3.2      0      31
Processing:  1003 1087  78.8   1063    1620
Waiting:     1003 1085  78.8   1062    1619
Total:       1003 1087  79.5   1064    1621

Percentage of the requests served within a certain time (ms)
  50%   1064
  66%   1093
  75%   1117
  80%   1132
  90%   1172
  95%   1226
  98%   1345
  99%   1442
 100%   1621 (longest request)
```

### Imperative
> ab -n 10000 -c 300 "http://localhost:7272/data"

```
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            7272

Document Path:          /data
Document Length:        67 bytes

Concurrency Level:      300
Time taken for tests:   63.557 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1720000 bytes
HTML transferred:       670000 bytes
Requests per second:    157.34 [#/sec] (mean)
Time per request:       1906.699 [ms] (mean)
Time per request:       6.356 [ms] (mean, across all concurrent requests)
Transfer rate:          26.43 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   4.0      0      39
Processing:  1044 1850 726.8   1702    9693
Waiting:     1005 1850 726.8   1702    9693
Total:       1044 1851 726.7   1703    9694

Percentage of the requests served within a certain time (ms)
  50%   1703
  66%   1789
  75%   1866
  80%   1921
  90%   2132
  95%   2660
  98%   4704
  99%   5675
 100%   9694 (longest request)
```