## Wireshark analyzing GRPC message
- https://www.imlc.me/how-to-inspect-grpc-with-wireshark/
- https://www.ridingthecrest.com/blog/2018/10/29/wireshark-getting-started.html

```shell
sudo apt update -y && sudo apt install wireshark -y

sudo wireshark

# config http, http2 (or then decode tcp connection as http2)
# config gRPC
# config protobuf
# Edit -> Preferences -> Protocols
```