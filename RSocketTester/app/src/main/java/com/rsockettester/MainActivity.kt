package com.rsockettester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.CompositeByteBuf
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.features.websocket.*
//import io.ktor.utils.io.core.*
//import io.rsocket.kotlin.RSocket
//import io.rsocket.kotlin.RSocketRequestHandler
//import io.rsocket.kotlin.core.RSocketConnector
//import io.rsocket.kotlin.core.WellKnownMimeType
//import io.rsocket.kotlin.metadata.RoutingMetadata
//import io.rsocket.kotlin.metadata.compositeMetadata
//import io.rsocket.kotlin.metadata.metadata
//import io.rsocket.kotlin.metadata.toPacket
//import io.rsocket.kotlin.payload.Payload
//import io.rsocket.kotlin.payload.PayloadMimeType
//import io.rsocket.kotlin.payload.buildPayload
//import io.rsocket.kotlin.payload.data
//import io.rsocket.kotlin.transport.ktor.client.RSocketSupport
//import io.rsocket.kotlin.transport.ktor.client.rSocket
//import io.netty.buffer.ByteBufAllocator
//import io.netty.buffer.ByteBufUtil
//import io.netty.buffer.CompositeByteBuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import io.rsocket.util.DefaultPayload
import io.rsocket.Payload
import reactor.core.publisher.Flux
import io.rsocket.core.RSocketConnector
import io.rsocket.RSocket
import io.rsocket.metadata.CompositeMetadataCodec
import io.rsocket.metadata.RoutingMetadata
import io.rsocket.metadata.TaggingMetadataCodec
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.transport.netty.client.WebsocketClientTransport
import io.rsocket.util.ByteBufPayload
import java.net.URI


class MainActivity : AppCompatActivity() {

    private val hostUrl = "ws://192.168.0.245:7000/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pathEditText = findViewById<EditText>(R.id.connection_address)
        pathEditText.setText("name")
        val messageEditText = findViewById<EditText>(R.id.text_to_send)
        messageEditText.setText("user")
        val resultView = findViewById<TextView>(R.id.result)

        val send = findViewById<AppCompatButton>(R.id.send)
        send.setOnClickListener {
            val route = pathEditText.text.toString()
            val message = messageEditText.text.toString()
            resultView.setText(connect(route, message))
        }
    }

    /**
     * The method to create the default Payload from the route and message data.
     * @param route represents the route set on the server's controller @MessageMapping param
     * @param message represents the data to send at the specified route
     */
    private fun getPayload(route: String, message: String): Payload {
        val compositeByteBuf = CompositeByteBuf(ByteBufAllocator.DEFAULT, false, 1);
        val routingMetadata = TaggingMetadataCodec.createRoutingMetadata(ByteBufAllocator.DEFAULT, listOf(route))
        CompositeMetadataCodec.encodeAndAddMetadata(compositeByteBuf, ByteBufAllocator.DEFAULT, WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, routingMetadata.content)
        val md = ByteBufUtil.getBytes(compositeByteBuf)
        return DefaultPayload.create(message.toByteArray(), md)
    }

    private fun connect(route: String, message: String): String? = runBlocking {
        withContext(Dispatchers.IO) {

            val ws: WebsocketClientTransport =
                WebsocketClientTransport.create(URI.create(hostUrl))
            val clientRSocket = RSocketConnector.connectWith(ws).block()
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


    /*private fun connect(route: String, message: String): String? = runBlocking {
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
    }*/
}

