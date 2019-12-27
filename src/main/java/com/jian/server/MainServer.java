package com.jian.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @auther JianLinWei
 * @date 2019-12-05 16:57
 */
public class MainServer {


    public static void main(String[] args) throws InterruptedException {

        startServer("", 1000);

    }


    public static void  startServer(String hostName , int port) throws InterruptedException {
        ServerBootstrap  serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boss ,worker).channel(NioServerSocketChannel.class)
                                                 .childHandler(new ChannelInitializer<SocketChannel>() {
                                                     @Override
                                                     protected void initChannel(SocketChannel socketChannel) throws Exception {
                                                         ChannelPipeline pipeline = socketChannel.pipeline();
                                                        /* pipeline.addLast(new StringDecoder());
                                                       pipeline.addLast( new StringEncoder());*/
                                                         pipeline.addLast(new ObjectEncoder());
                                                         pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE , ClassResolvers.cacheDisabled(null)));
                                                         pipeline.addLast(new MainHandler());
                                                     }
                                                 }) ;
        ChannelFuture future  = serverBootstrap.bind(port).sync();
        future.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }


}
