package com.test.rsocketserver

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class NettyWebServerFactoryPortCustomizer : WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    override fun customize(serverFactory: NettyReactiveWebServerFactory) {
        serverFactory.port = 8888
    }
}