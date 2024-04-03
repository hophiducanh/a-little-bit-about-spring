## References
- https://soubiran.dev/posts/real-time-without-websocket

---
---

### Websocket
![](websocket1.png)

WebSocket is not HTTP. WebSocket is a protocol on its own. This is very important to know because this has consequences on how we can use it.


First, WebSocket is not native on Node.js. We have to use an external library to use it. This could become a big deal if we have to integrate it into a complex project.

Second, WebSocket does not handle reconnection. If the connection is lost, there is no built-in mechanism to reconnect. We have to handle it ourselves. In another way and despite its native support in browsers, we have to use it with a library to handle reconnection. This starts to be a lot of complexity and code to maintain.

Finally, WebSocket is not HTTP, and in your web server, you will need to handle two protocols and create a bridge between them. The bigger part is about authentication and authorization. This could result in a lot of logic to create and maintain despite the fact that we could already have an authentication logic for HTTP. This also implies having a custom logic on the client to handle authentication and authorization. This is because HTTP is stateless and WebSocket is not.

The thing I would love to hear when I was starting with WebSocket is that not every data needs to be real-time and simpler things are better than nothing.


---

### Server-Sent-Events
![](sse.png)