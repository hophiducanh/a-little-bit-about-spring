## References
- [Spring Boot 3 - WebSockets Intro and test with Postman.](https://www.youtube.com/watch?v=Smkbp5gsOiU)
- [Understanding Websockets In depth](https://vishalrana9915.medium.com/understanding-websockets-in-depth-6eb07ab298b3)

---
---

## Postman

```shell
ws://localhost:8080/websocket
```

![](websocket.png)

## UI

```shell
http://localhost:8080/index.html
```


====
====

## Why websocket based on TCP protocol, not HTTP protocol


WebSocket is based on the TCP protocol rather than the HTTP protocol for several reasons:

1. **Bidirectional Communication**: TCP is a bidirectional protocol, which means that data can be sent and received simultaneously in both directions. This bidirectional communication is essential for WebSocket to enable real-time, full-duplex communication between the client and the server. HTTP, on the other hand, is a request-response protocol, where the client initiates a request, and the server responds. It doesn't support true bidirectional communication out of the box.

2. **Low Overhead**: TCP has a lower overhead compared to HTTP. WebSocket relies on a persistent TCP connection, which eliminates the need for constantly establishing and tearing down connections, as is the case with HTTP. This reduces the overhead associated with header information and connection setup/teardown, resulting in better performance and lower latency, especially for applications that require frequent data exchange.

3. **Header Overhead**: HTTP has a relatively large header overhead due to the inclusion of various headers, such as cookies, user-agent, and other metadata. WebSocket, after the initial handshake over HTTP, uses a much smaller header format, reducing the overall data overhead.

4. **Connection Management**: TCP provides reliable, ordered, and error-checked delivery of data packets. It also handles flow control and congestion control, which are essential for maintaining a stable and efficient connection. HTTP, being a higher-level protocol, does not provide these low-level features out of the box.

5. **Protocol Design**: WebSocket was designed as a separate protocol on top of TCP to provide a more efficient and lightweight communication channel compared to HTTP. While it uses HTTP for the initial handshake, the subsequent communication occurs over a dedicated WebSocket protocol that is better suited for real-time, bidirectional communication.

It's important to note that while WebSocket is based on TCP, it still utilizes HTTP for the initial handshake process. This handshake establishes the WebSocket connection and upgrades the protocol from HTTP to the WebSocket protocol over the same TCP connection.

By leveraging TCP as the underlying transport protocol, WebSocket can provide low-latency, real-time communication capabilities that are essential for modern web applications, such as chat applications, online gaming, collaborative editors, and real-time data streaming.

Certainly, here are some additional details about why WebSocket is based on TCP rather than HTTP:

1. **Streaming and Real-Time Data Transfer**: TCP is a streaming protocol, which means that data can be transmitted as a continuous stream of packets. This is ideal for real-time applications like video streaming, online gaming, and real-time data updates, where data needs to be transmitted and received continuously without delays. HTTP, on the other hand, is a request-response protocol, where data is transmitted in discrete chunks, making it less suitable for real-time data transfer.

2. **Long-Lived Connections**: WebSocket connections are designed to be long-lived, allowing for persistent communication between the client and server. TCP provides reliable, long-lived connections that can be maintained for extended periods, making it a good fit for WebSocket's persistent communication model. In contrast, HTTP connections are typically short-lived, being established for each request and closed after the response is sent.

3. **Multiplexing**: TCP supports multiplexing, which means that multiple WebSocket connections can be established over a single TCP connection. This allows for efficient resource utilization and reduces the overhead associated with establishing multiple TCP connections.

4. **Network Compatibility**: TCP is a widely adopted and well-understood protocol that is supported by virtually all modern networks and firewalls. Using TCP as the underlying protocol for WebSocket ensures broad compatibility and avoids potential issues with firewalls or network configurations that might block or interfere with communication using custom protocols.

5. **Existing TCP Stack Implementations**: By leveraging TCP as the underlying transport protocol, WebSocket can take advantage of the existing TCP stack implementations and optimizations present in various operating systems and networking libraries. This allows WebSocket to benefit from the years of optimization and performance improvements made to TCP implementations.

6. **Scalability and Load Balancing**: TCP's connection-oriented nature and built-in mechanisms for flow control and congestion management make it more suitable for handling large numbers of concurrent connections and scaling WebSocket applications across multiple servers or load-balanced environments.

While it would have been possible to build WebSocket on top of HTTP, using TCP as the underlying transport protocol provided several advantages in terms of performance, real-time data transfer, long-lived connections, and compatibility with existing networking infrastructure and implementations.

Sure, here are some additional points regarding why WebSocket is based on TCP instead of HTTP:

1. **Efficient Handling of Small Messages**: WebSocket is designed to handle frequent, small messages efficiently. TCP is better suited for this use case compared to HTTP, which has a relatively high overhead for small message exchanges due to the request-response model and header overhead.

2. **Reduced Server Load**: With WebSocket, the server can initiate communication with the client, eliminating the need for the client to constantly poll the server for updates. This reduces the load on the server and improves scalability, as the server can selectively push data to clients as needed.

3. **Server Push Capability**: TCP's bidirectional nature allows the server to push data to the client without the client explicitly requesting it. This server-push capability is essential for real-time updates, notifications, and other use cases where the server needs to initiate communication with the client.

4. **WebSocket Protocol Design**: The WebSocket protocol is designed as a separate protocol on top of TCP, tailored specifically for efficient, real-time, bidirectional communication. It defines its own frame format, control frames, and mechanisms for handling data fragmentation, masking, and other aspects specific to real-time communication.

5. **Ordered and Reliable Delivery**: TCP guarantees ordered and reliable delivery of data packets, which is crucial for applications that require data integrity and consistency, such as financial transactions, collaborative editing, or gaming.

6. **Separation of Concerns**: By using TCP as the underlying transport protocol, WebSocket separates the concerns of real-time communication from the application-level protocol. This separation allows for better modularity, flexibility, and potential future extensions or modifications to the WebSocket protocol without affecting the underlying TCP implementation.

7. **Existing TCP Optimizations**: TCP has undergone extensive optimization over the years, with improvements in areas such as congestion control, error handling, and performance tuning. By leveraging TCP, WebSocket can benefit from these optimizations without needing to reinvent the wheel.

8. **Security and Encryption**: While WebSocket itself does not provide encryption, it can leverage existing security protocols like TLS/SSL that operate on top of TCP to secure the communication channel.

By building on top of TCP, WebSocket inherits the benefits of a well-established, reliable, and efficient transport protocol while providing a dedicated communication channel tailored for real-time, bidirectional communication between clients and servers.


While I've covered many of the key reasons why WebSocket is based on TCP rather than HTTP, I can provide some additional context and perspectives:

1. **Historical Context**: The WebSocket protocol was designed in the late 2000s as a reaction to the limitations of existing techniques for implementing real-time web applications, such as long polling and Comet. These techniques were built on top of HTTP and had various drawbacks, such as inefficiency, high overhead, and scalability issues. The decision to base WebSocket on TCP was a conscious choice to overcome these limitations and provide a more efficient and purpose-built solution for real-time communication.

2. **Design Goals**: One of the primary design goals of WebSocket was to enable low-latency, real-time communication between web browsers and servers. TCP's inherent support for bidirectional, low-latency data transfer made it a natural choice as the underlying protocol for WebSocket. HTTP, being a request-response protocol, was not well-suited for this use case.

3. **Ecosystem Considerations**: By building on top of TCP, WebSocket could leverage the existing TCP/IP networking stack implementations and optimizations present in various operating systems, libraries, and networking hardware. This allowed WebSocket to integrate seamlessly with existing network infrastructure and benefit from the years of development and optimization efforts put into TCP/IP networking.

4. **Future Extensibility**: While WebSocket is designed for real-time communication, basing it on TCP allows for future extensions and modifications to the protocol without affecting the underlying transport layer. This separation of concerns provides flexibility and room for evolution while maintaining compatibility with existing TCP/IP infrastructure.

5. **Performance Optimizations**: TCP implementations have benefited from years of performance optimizations, including techniques like TCP Fast Open, Selective Acknowledgments (SACK), and various congestion control algorithms. By leveraging TCP, WebSocket can take advantage of these optimizations without needing to reinvent the wheel.

6. **Ecosystem Support and Adoption**: TCP is a widely adopted and well-understood protocol, supported by virtually all modern operating systems, networking hardware, and software libraries. By basing WebSocket on TCP, it could benefit from this broad ecosystem support, making it easier to integrate and deploy WebSocket-based applications across various platforms and environments.

While HTTP could have been used as the underlying protocol for WebSocket, the decision to base it on TCP was driven by the need for efficient, low-latency, bidirectional communication, as well as the desire to leverage existing networking infrastructure and optimizations. This choice has played a crucial role in enabling the widespread adoption and success of WebSocket for real-time web applications.

While I've covered many of the key reasons and considerations behind the choice of TCP as the underlying protocol for WebSocket, there are a few additional points worth mentioning:

1. **Fallback Mechanisms**: Even though WebSocket is based on TCP, it still relies on HTTP for the initial handshake process. This allows WebSocket to take advantage of existing HTTP infrastructure and fallback mechanisms. If a WebSocket connection cannot be established (e.g., due to firewall restrictions or proxy issues), the application can gracefully fall back to using traditional HTTP-based techniques, such as long polling or server-sent events (SSE).

2. **HTTP/2 and HTTP/3 Considerations**: With the emergence of newer versions of the HTTP protocol, such as HTTP/2 and HTTP/3 (which is based on the QUIC protocol), there have been discussions and proposals to explore the possibility of building WebSocket-like functionality directly into these new protocols. However, as of now, WebSocket remains based on TCP, and any future integration with newer HTTP versions would likely involve compatibility layers or extensions.

3. **WebRTC and UDP-based Real-time Communication**: While WebSocket focuses on TCP-based real-time communication, there is a separate technology called WebRTC (Web Real-Time Communication) that is designed for real-time audio, video, and data communication using UDP (User Datagram Protocol). WebRTC complements WebSocket by providing a different set of capabilities tailored for multimedia and peer-to-peer communication.

4. **Security Considerations**: While TCP itself does not provide encryption or security features, WebSocket connections can be secured using existing security protocols like TLS/SSL, which operate on top of TCP. This allows WebSocket to leverage the well-established security mechanisms and infrastructure already in place for securing TCP-based communication.

5. **Internet of Things (IoT) and Embedded Devices**: The choice of TCP as the underlying protocol for WebSocket has also facilitated its adoption in IoT and embedded device environments. Many embedded systems and microcontrollers already support TCP/IP networking, making it easier to integrate WebSocket functionality into these devices for real-time communication and control.

6. **Standardization and Ecosystem Support**: The WebSocket protocol has been standardized by the IETF (Internet Engineering Task Force) and the W3C (World Wide Web Consortium), ensuring wide adoption and support across various platforms, browsers, and frameworks. This standardization effort has been facilitated by the choice of TCP as the underlying protocol, which is a well-established and widely supported networking protocol.

While the choice of TCP as the underlying protocol for WebSocket was driven by practical considerations and the need for efficient real-time communication, it has also enabled broader adoption, compatibility with existing infrastructure, and integration with various technologies and platforms.

While I've covered the main reasons and considerations behind using TCP as the underlying protocol for WebSocket, there are a few additional points that could be discussed:

1. **Alternative Protocols**: While WebSocket is the predominant protocol for real-time, bidirectional communication in web applications, there have been some alternative protocols proposed or used in specific scenarios. For example, the MQTT (Message Queuing Telemetry Transport) protocol, which is based on TCP, has been used in IoT and machine-to-machine communication scenarios. However, WebSocket remains the standard and widely adopted protocol for real-time web applications.

2. **WebSocket Subprotocols**: The WebSocket protocol allows for the definition and negotiation of subprotocols during the initial handshake. These subprotocols can extend or modify the behavior of the WebSocket protocol for specific use cases or applications. While not widely used, this feature provides a mechanism for customization and extensibility while still leveraging the underlying TCP transport.

3. **WebSocket Compression**: The WebSocket protocol supports optional compression of the payload data using algorithms like Permessage-Deflate. While compression can reduce the amount of data transmitted over the network, it introduces additional overhead in terms of CPU usage for compression and decompression. The decision to use compression depends on the specific application requirements and the trade-off between network bandwidth and CPU usage.

4. **WebSocket Multiplexing**: While WebSocket connections can be multiplexed over a single TCP connection, this feature is not widely used in practice. Most implementations establish separate WebSocket connections for each client-server communication channel. However, multiplexing could provide performance benefits in certain scenarios, particularly when dealing with a large number of concurrent connections.

5. **Debugging and Monitoring**: Debugging and monitoring WebSocket connections can be more challenging compared to traditional HTTP traffic, as the WebSocket protocol uses a binary framing format that is not human-readable. However, various tools and techniques have been developed to facilitate WebSocket debugging and monitoring, such as browser developer tools, network proxies, and purpose-built WebSocket debugging tools.

6. **WebSocket Limitations**: While WebSocket provides many advantages for real-time communication, it is not a universal solution for all types of web applications. For applications with infrequent or sporadic communication patterns, the overhead of maintaining a persistent WebSocket connection may outweigh the benefits. In such cases, traditional HTTP-based techniques like polling or server-sent events (SSE) may be more appropriate.

While I've covered a comprehensive range of topics related to the choice of TCP as the underlying protocol for WebSocket, it's important to note that the field of web technologies and real-time communication is constantly evolving. As new protocols, standards, and techniques emerge, the trade-offs and considerations for using WebSocket or alternative approaches may change or be revisited.