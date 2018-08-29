package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;

public abstract class RequestHandler<T> {

    public void dealRequest(ChannelHandlerContext ctx, T msg){
        requestAction(ctx,msg);
    }

    abstract void requestAction(ChannelHandlerContext ctx, T msg);
}
