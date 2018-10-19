package server.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.protocol.WsProtocol;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EchoServerHandler extends ChannelHandlerAdapter {
	private static final Logger LOGGER=LoggerFactory.getLogger(EchoServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WsProtocol wsProtocol=(WsProtocol)msg;
		System.out.println("--------------收到包------------------");
		System.out.println("头部："+wsProtocol.getHead_data());
		System.out.println("设备id高8位："+wsProtocol.getDeviceIdHigh());
		System.out.println("设备id低8位："+wsProtocol.getDeviceIdLow());
		System.out.println("回话id高8位："+wsProtocol.getSessionIdHigh());
		System.out.println("回话id低8位："+wsProtocol.getSessionIdLow());
		System.out.println("序号Id："+wsProtocol.getSequenceId());
		System.out.println("保留位："+wsProtocol.getReserve());
		System.out.println("数据长度:"+wsProtocol.getContentLength());
		System.out.print("数据：");
		for(int i=0;i<wsProtocol.getContent().length;i++){
			System.out.print(" "+wsProtocol.getContent()[i]);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
