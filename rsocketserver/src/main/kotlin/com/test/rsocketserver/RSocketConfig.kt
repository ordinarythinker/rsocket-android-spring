package com.test.main

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.codec.Decoder
import org.springframework.core.codec.Encoder
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import java.net.URI

@Configuration
class RSocketConfig {
    @Bean
    fun rSocketStrategies(): RSocketStrategies {
        return RSocketStrategies.builder()
                .encoders { encoders: MutableList<Encoder<*>?> -> encoders.add(Jackson2JsonEncoder()) }
                .decoders { decoders: MutableList<Decoder<*>?> -> decoders.add(Jackson2JsonDecoder()) }
                .build()
    }
}