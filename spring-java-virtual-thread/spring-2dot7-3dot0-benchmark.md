## [Virtual threads in Spring Boot with Java 19](https://medium.com/@egorponomarev/virtual-threads-in-spring-boot-with-java-19-ea98e1725058)


## Spring 2.7 + Tomcat 9 + Java 17
> ab -n 1000 -c 1000 http://localhost:8118/test

![image](https://user-images.githubusercontent.com/22516811/271817386-4aa7934d-c752-4ef5-a992-9e626b10c772.png)

```shell
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Completed 900 requests
Completed 1000 requests
Finished 1000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8118

Document Path:          /test
Document Length:        0 bytes

Concurrency Level:      1000
Time taken for tests:   31.180 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      92000 bytes
HTML transferred:       0 bytes
Requests per second:    32.07 [#/sec] (mean)
Time per request:       31179.945 [ms] (mean)
Time per request:       31.180 [ms] (mean, across all concurrent requests)
Transfer rate:          2.88 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0  424 479.7     67    1053
Processing:  5025 15031 6766.3  15370   24811
Waiting:     5024 15030 6766.5  15370   24811
Total:       5093 15455 7167.3  15393   25861

Percentage of the requests served within a certain time (ms)
  50%  15393
  66%  20490
  75%  20694
  80%  20797
  90%  25668
  95%  25686
  98%  25796
  99%  25821
 100%  25861 (longest request)
```

## Spring 3.0 + Tomcat 10 + Java 21
> ab -n 1000 -c 1000 http://localhost:8119/test

![image](https://user-images.githubusercontent.com/22516811/271817306-f3b0946e-3513-4960-a913-53b0e366342a.png)

```shell
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Completed 900 requests
Completed 1000 requests
Finished 1000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8119

Document Path:          /test
Document Length:        0 bytes

Concurrency Level:      1000
Time taken for tests:   11.532 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      92000 bytes
HTML transferred:       0 bytes
Requests per second:    86.72 [#/sec] (mean)
Time per request:       11532.001 [ms] (mean)
Time per request:       11.532 [ms] (mean, across all concurrent requests)
Transfer rate:          7.79 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0  711 457.0   1013    1053
Processing:  5023 5159  66.9   5155    5268
Waiting:     5012 5158  67.6   5153    5268
Total:       5067 5870 461.4   6128    6315

Percentage of the requests served within a certain time (ms)
  50%   6128
  66%   6187
  75%   6209
  80%   6250
  90%   6275
  95%   6286
  98%   6302
  99%   6306
 100%   6315 (longest request)
```