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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import io.rsocket.util.DefaultPayload
import io.rsocket.Payload
import io.rsocket.core.RSocketConnector
import io.rsocket.metadata.CompositeMetadataCodec
import io.rsocket.metadata.TaggingMetadataCodec
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.transport.netty.client.WebsocketClientTransport
import java.net.URI
import io.netty.buffer.ByteBuf
import io.rsocket.metadata.RoutingMetadata
import java.util.List


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



}
