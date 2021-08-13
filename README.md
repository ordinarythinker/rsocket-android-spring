# rsocket-android-spring

<strong>RSocket Android + Spring Boot</strong> back-end routing error: No handler for destination ''

I get the ApplicationErrorException: No handler for destination '' trying to connet to my web server (spring boot) from android code using RSocket. As a transport I use websockets.

On the server side I use:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

On the clint I used both:

```
implementation 'io.rsocket:rsocket-core:1.1.1'
implementation 'io.rsocket:rsocket-transport-netty:1.1.1'
```

and

```
implementation 'io.rsocket.kotlin:rsocket-core:0.13.1'
implementation 'io.rsocket.kotlin:rsocket-transport-ktor:0.13.1'
implementation 'io.rsocket.kotlin:rsocket-transport-ktor-client:0.13.1'
implementation "io.ktor:ktor-client-cio:1.6.1"
```

Both of Ktor and Netty had given me the same error. The logcat is the following

Android:
```
ApplicationErrorException (0x201): No handler for destination ''
        at io.rsocket.exceptions.Exceptions.from(Exceptions.java:76)
        at io.rsocket.core.RSocketRequester.handleFrame(RSocketRequester.java:261)
        at io.rsocket.core.RSocketRequester.handleIncomingFrames(RSocketRequester.java:211)
        at io.rsocket.core.RSocketRequester.$r8$lambda$kDn7LIfo960b6cXO3SLu8QVkTAE(Unknown Source:0)
        at io.rsocket.core.RSocketRequester$$ExternalSyntheticLambda2.accept(Unknown Source:4)
        at reactor.core.publisher.LambdaSubscriber.onNext(LambdaSubscriber.java:160)
        at io.rsocket.core.ClientServerInputMultiplexer$InternalDuplexConnection.onNext(ClientServerInputMultiplexer.java:248)
        at io.rsocket.core.ClientServerInputMultiplexer.onNext(ClientServerInputMultiplexer.java:129)
        at io.rsocket.core.ClientServerInputMultiplexer.onNext(ClientServerInputMultiplexer.java:48)
        at reactor.core.publisher.FluxMap$MapSubscriber.onNext(FluxMap.java:120)
        at reactor.netty.channel.FluxReceive.onInboundNext(FluxReceive.java:365)
        at reactor.netty.channel.ChannelOperations.onInboundNext(ChannelOperations.java:401)
        at reactor.netty.http.client.HttpClientOperations.onInboundNext(HttpClientOperations.java:707)
        at reactor.netty.http.client.WebsocketClientOperations.onInboundNext(WebsocketClientOperations.java:161)
        at reactor.netty.channel.ChannelOperationsHandler.channelRead(ChannelOperationsHandler.java:94)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
        at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:324)
        at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:296)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
        at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
        at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
        at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)
        at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:719)
        at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:655)
        at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:581)
        at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493)
        at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.lang.Thread.run(Thread.java:764)
    	Suppressed: java.lang.Exception: #block terminated with an error
        at reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:99)
        at reactor.core.publisher.Mono.block(Mono.java:1703)
        at com.rsockettester.MainActivity$connect$1$1.invokeSuspend(MainActivity.kt:86)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
        at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
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

The code I used to connect from Android, using 'io.rsocket:rsocket-transport-netty:1.1.1' is the following:
```
    private fun connect(route: String, message: String): String? = runBlocking {
        withContext(Dispatchers.IO) {

            val ws: WebsocketClientTransport =
                WebsocketClientTransport.create(URI.create(hostUrl))
            val clientRSocket = RSocketConnector.connectWith(ws).block()
            
            return@withContext try {
            	
            	val compositeByteBuf = CompositeByteBuf(ByteBufAllocator.DEFAULT, false, 1);
        		val routingMetadata = TaggingMetadataCodec.createRoutingMetadata(ByteBufAllocator.DEFAULT, listOf(route))
        		CompositeMetadataCodec.encodeAndAddMetadata(compositeByteBuf, ByteBufAllocator.DEFAULT, 
        								WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, routingMetadata.content)
        		val md = ByteBufUtil.getBytes(compositeByteBuf)
        		val payload = DefaultPayload.create(message.toByteArray(), md)
                
                val s = clientRSocket?.requestResponse(payload)
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

The code used to connect with Ktor is the the following:
```
	private fun connect(route: String, message: String): String? = runBlocking {
        val client = HttpClient(CIO) { //create and configure ktor client
            install(WebSockets)
            install(RSocketSupport) {
                connector = RSocketConnector {
                    connectionConfig {
                        payloadMimeType = PayloadMimeType(
                            data = "application/json",
                            metadata = "application/json"
                        )
                    }

                    acceptor {
                        RSocketRequestHandler {
                            requestResponse { it } //echo request payload
                        }
                    }
                }
            }
            expectSuccess = false
        }

        var rSocket: RSocket? = try {
            client.rSocket(hostUrl)
        } catch (e: Exception) {
            Log.e("net", "RSocket cannot connect:", e)
            return@runBlocking "RSocket cannot connect: ${e.asString()}"
        }

        return@runBlocking try {
            val response = rSocket?.requestResponse(
                buildPayload {
                    data(message)
                    compositeMetadata {
                        add(WellKnownMimeType.MessageRSocketRouting, RoutingMetadata(route).toPacket())
                    }
                }
            )
            Log.d("net", "reached response")
            response?.let { it.data.readUTF8Line() }
        } catch (e: Exception) {
            e.printStackTrace()
            "RSocket cannot connect: ${e.asString()}"
        }
    }
```

As I mentioned above, both approaches lead to the same result: No handler for destination ''
Worth to mention, this problem is absent when I use the same routing from another Spring Boot client.

I created these sample projects to help reproduce this error.

<strong>Steps to reproduce:</strong>
1. Clone or download the github project https://github.com/mr-nexter/rsocket-android-spring
2. Run the spring boot server
3. Edit the hostUrl variable providing the the correct IP-address of your PC (!)
4. Run the Android app and click the 'Send' button

If you wish to switch to Ktor from Netty on Android, you can use the commented method 

Also when rsocket is disposed on the client I get the following error on the server-side:
```
Caused by: java.util.concurrent.CancellationException: Disposed
	at io.rsocket.internal.UnboundedProcessor.dispose(UnboundedProcessor.java:545) ~[rsocket-core-1.1.1.jar:na]
	at io.rsocket.transport.netty.WebsocketDuplexConnection.doOnClose(WebsocketDuplexConnection.java:72) ~[rsocket-transport-netty-1.1.1.jar:na]
	at io.rsocket.internal.BaseDuplexConnection.lambda$new$0(BaseDuplexConnection.java:30) ~[rsocket-core-1.1.1.jar:na]
	at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.runFinally(FluxDoFinally.java:163) ~[reactor-core-3.4.8.jar:3.4.8]
	at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onComplete(FluxDoFinally.java:146) ~[reactor-core-3.4.8.jar:3.4.8]
	at reactor.core.publisher.SinkEmptyMulticast$VoidInner.onComplete(SinkEmptyMulticast.java:227) ~[reactor-core-3.4.8.jar:3.4.8]
	at reactor.core.publisher.SinkEmptyMulticast.tryEmitEmpty(SinkEmptyMulticast.java:70) ~[reactor-core-3.4.8.jar:3.4.8]
	at reactor.core.publisher.SinkEmptySerialized.tryEmitEmpty(SinkEmptySerialized.java:46) ~[reactor-core-3.4.8.jar:3.4.8]
	at io.rsocket.internal.BaseDuplexConnection.dispose(BaseDuplexConnection.java:51) ~[rsocket-core-1.1.1.jar:na]
	at io.rsocket.transport.netty.WebsocketDuplexConnection.lambda$new$0(WebsocketDuplexConnection.java:54) ~[rsocket-transport-netty-1.1.1.jar:na]
	at io.netty.util.concurrent.DefaultPromise.notifyListener0(DefaultPromise.java:578) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.notifyListeners0(DefaultPromise.java:571) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.notifyListenersNow(DefaultPromise.java:550) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.notifyListeners(DefaultPromise.java:491) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.setValue0(DefaultPromise.java:616) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.setSuccess0(DefaultPromise.java:605) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.trySuccess(DefaultPromise.java:104) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPromise.trySuccess(DefaultChannelPromise.java:84) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannel$CloseFuture.setClosed(AbstractChannel.java:1182) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.doClose0(AbstractChannel.java:773) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.close(AbstractChannel.java:749) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.close(AbstractChannel.java:620) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.close(DefaultChannelPipeline.java:1352) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeClose(AbstractChannelHandlerContext.java:622) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.close(AbstractChannelHandlerContext.java:606) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.close(AbstractChannelHandlerContext.java:472) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPipeline.close(DefaultChannelPipeline.java:957) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannel.close(AbstractChannel.java:244) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at reactor.netty.DisposableChannel.dispose(DisposableChannel.java:71) ~[reactor-netty-core-1.0.9.jar:1.0.9]
	at reactor.netty.channel.ChannelOperations.dispose(ChannelOperations.java:203) ~[reactor-netty-core-1.0.9.jar:1.0.9]
	at reactor.netty.transport.ServerTransport$ChildObserver.onStateChange(ServerTransport.java:474) ~[reactor-netty-core-1.0.9.jar:1.0.9]
	at reactor.netty.channel.ChannelOperations.terminate(ChannelOperations.java:478) ~[reactor-netty-core-1.0.9.jar:1.0.9]
	at reactor.netty.http.server.WebsocketServerOperations.lambda$onInboundNext$2(WebsocketServerOperations.java:156) ~[reactor-netty-http-1.0.9.jar:1.0.9]
	at io.netty.util.concurrent.DefaultPromise.notifyListener0(DefaultPromise.java:578) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.notifyListenersNow(DefaultPromise.java:552) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.notifyListeners(DefaultPromise.java:491) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.DefaultPromise.addListener(DefaultPromise.java:184) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPromise.addListener(DefaultChannelPromise.java:95) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPromise.addListener(DefaultChannelPromise.java:30) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at reactor.netty.http.server.WebsocketServerOperations.sendCloseNow(WebsocketServerOperations.java:260) ~[reactor-netty-http-1.0.9.jar:1.0.9]
	at reactor.netty.http.server.WebsocketServerOperations.onInboundNext(WebsocketServerOperations.java:156) ~[reactor-netty-http-1.0.9.jar:1.0.9]
	at reactor.netty.channel.ChannelOperationsHandler.channelRead(ChannelOperationsHandler.java:93) ~[reactor-netty-core-1.0.9.jar:1.0.9]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.rsocket.transport.netty.server.BaseWebsocketServerTransport$PongHandler.channelRead(BaseWebsocketServerTransport.java:63) ~[rsocket-transport-netty-1.1.1.jar:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:324) ~[netty-codec-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:296) ~[netty-codec-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:719) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:655) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:581) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493) ~[netty-transport-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:986) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.66.Final.jar:4.1.66.Final]
	at java.base/java.lang.Thread.run(Thread.java:832) ~[na:na]
```
