# rsocket-android-spring

<strong>RSocket Android + Spring Boot back-end sample</strong>

On the server side is used:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

On the client is used:

```
implementation 'io.rsocket:rsocket-core:1.1.1'
implementation 'io.rsocket:rsocket-transport-netty:1.1.1'
```

Here is the controller code, used on the back-end:
```
@Controller
class MainController {

    @MessageMapping("hello")
    fun hello() = "Hello!"

    @MessageMapping("name")
    fun helloName(name: String) = "Hello, $name!"
}
```

The code is used to connect from Android, using 'io.rsocket:rsocket-transport-netty:1.1.1' is the following:
```
    private fun getPayload(route: String, message: String): Payload {
        val metadata = ByteBufAllocator.DEFAULT.compositeBuffer()
        val routingMetadata =
            TaggingMetadataCodec.createRoutingMetadata(ByteBufAllocator.DEFAULT, listOf(route))
        CompositeMetadataCodec.encodeAndAddMetadata(
            metadata,
            ByteBufAllocator.DEFAULT,
            WellKnownMimeType.MESSAGE_RSOCKET_ROUTING,
            routingMetadata.content
        )
        val data = ByteBufAllocator.DEFAULT.buffer().writeBytes(message.toByteArray())

        return DefaultPayload.create(data, metadata)
    }

    private fun connect(route: String, message: String): String? = runBlocking {
        withContext(Dispatchers.IO) {

            val ws: WebsocketClientTransport =
                WebsocketClientTransport.create(URI.create(hostUrl))
            val clientRSocket = RSocketConnector.create()
                //metadata header needs to be specified
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string)
                // value of spring.rsocket.server.port eg 7000
                .connect(ws)
                .block()
            return@withContext try {
                val s = clientRSocket?.requestResponse(getPayload(route, message))
                s?.block()?.dataUtf8
            } catch (e: Exception) {
                Log.e("net", "RSocket cannot connect: ", e)
                e.asString()
            } finally {
                clientRSocket?.dispose()
            }

        }
    }
```