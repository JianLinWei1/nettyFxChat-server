package com.jian.server;

import com.jian.util.CmdCodeUtil;
import com.jian.util.ReslutUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;
import org.json.JSONString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @auther JianLinWei
 * @date 2019-12-06 10:40
 */
public class MainHandler  extends ChannelInboundHandlerAdapter {

    private  static final Logger  logger = LoggerFactory.getLogger(MainHandler.class);

    private static final   HashMap<String , Integer>  IdMap = new HashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String  chanelId = ctx.channel().id().asShortText();
        logger.info("客户端上线；通道ID:{}" ,chanelId);


        ReslutUtil reslutUtil = new ReslutUtil();
        reslutUtil.setCode(CmdCodeUtil.CID);
        reslutUtil.setData(IdMap.get(chanelId));
        ctx.channel().writeAndFlush(reslutUtil);
        ctx.channel().flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String  chanelId = ctx.channel().id().asShortText();
        logger.info("客户端注册；通道ID:{}" ,chanelId);
        int id = IdMap.size();
        id++;
        IdMap.put(chanelId,id );
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("接收到客户端发来的消息：{} ，通道ID:{}", msg , ctx.channel().id());
        ctx.channel().writeAndFlush(msg);
        ctx.channel().flush();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

       logger.info("客户端关闭，通道ID：{}", ctx.channel().id());
       if(IdMap.containsKey(ctx.channel().id()))
           IdMap.remove(ctx.channel().id());
       ctx.close();

    }



}
