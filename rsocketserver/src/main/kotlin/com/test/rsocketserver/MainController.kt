package com.test.rsocketserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller

@Controller
class MainController {

    @MessageMapping("hello")
    fun hello() = "Hello!"

    @MessageMapping("name")
    fun helloName(name: String) = "Hello, $name!"
}