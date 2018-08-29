package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class NettyHttpHandler extends RequestHandler<FullHttpRequest> {
    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest msg) {

    }
}
