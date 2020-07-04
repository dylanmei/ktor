package io.ktor.server.netty

import io.netty.buffer.*
import io.netty.channel.*
import io.netty.handler.codec.*
import io.netty.handler.codec.haproxy.*

class ProxyProtocolHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        (msg as? ByteBuf)?.let {
            val detectionState = HAProxyMessageDecoder.detectProtocol(it)
            if (detectionState.state() == ProtocolDetectionState.DETECTED) {
                ctx!!.pipeline().addAfter("proxy-protocol", null, HAProxyMessageDecoder())
                ctx.pipeline().remove(this)
            }
        }

        super.channelRead(ctx, msg)
    }
}
