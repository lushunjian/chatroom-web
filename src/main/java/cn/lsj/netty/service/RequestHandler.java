package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public abstract class RequestHandler<T> {

    public void dealRequest(ChannelHandlerContext ctx, T msg, WebSocketServerHandshaker serverShakeHand){
        requestAction(ctx,msg,serverShakeHand);
    }

    abstract void requestAction(ChannelHandlerContext ctx, T msg ,WebSocketServerHandshaker serverShakeHand);
}
