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
    private static final  HashMap<String ,ChannelHandlerContext>  idCtxMap = new HashMap<>();
    private static final   HashMap<String , Integer>  IdMap = new HashMap<>();
    private static final   HashMap<Integer , ChannelHandlerContext>  pidCtxMap = new HashMap<>();




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String  chanelId = ctx.channel().id().asShortText();
        logger.info("客户端上线；通道ID:{}" ,chanelId);


        ReslutUtil reslutUtil = new ReslutUtil();
        reslutUtil.setCode(CmdCodeUtil.CID);
        reslutUtil.setData(IdMap.get(chanelId));
        ctx.channel().writeAndFlush(reslutUtil);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String  chanelId = ctx.channel().id().asShortText();
        logger.info("客户端注册；通道ID:{}" ,chanelId);
        int id = IdMap.size();
        id++;
        IdMap.put(chanelId,id );
        idCtxMap.put(chanelId, ctx);
        pidCtxMap.put(id, ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String clienid = ctx.channel().id().asShortText();
        logger.info("接收到客户端发来的消息：{} ，通道ID:{}", msg , clienid);

        if(msg instanceof  ReslutUtil){
            ReslutUtil reslutUtil = (ReslutUtil) msg;
          ChannelHandlerContext context =pidCtxMap.get(Integer.valueOf(reslutUtil.getCmd()));

          if(context != null && reslutUtil.getCode() == CmdCodeUtil.SEND_PRIVATE_OBJ ){
              context.channel().writeAndFlush(IdMap.get(clienid) + "给您的一条私信：\n" +reslutUtil.getData());
              ctx.channel().writeAndFlush(IdMap.get(clienid)+"给"+reslutUtil.getCmd()+"号，发送了一条私信：\n" +reslutUtil.getData());
          }else if(reslutUtil.getCode() == CmdCodeUtil.VIDEO_MSG){
              context.channel().writeAndFlush(reslutUtil.getData());

          }else{
              ctx.channel().writeAndFlush("对象ID不在线");
          }

        }
        if(msg instanceof String){
            for(ChannelHandlerContext context : idCtxMap.values()){
                context.channel().writeAndFlush(IdMap.get(clienid) + ":\n "+msg);
            }

        }
    }





    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      String chanelId = ctx.channel().id().asShortText();
       logger.info("客户端关闭，通道ID：{}", chanelId);

       if(IdMap.containsKey(chanelId))
           IdMap.remove(ctx.channel().id());
       ctx.close();

    }



}
