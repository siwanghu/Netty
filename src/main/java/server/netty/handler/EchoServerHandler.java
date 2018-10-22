package server.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;
import server.until.ServerUntil;

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
		echoPackage(wsProtocol);
		String deviceID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh()),ServerUntil.longToBytes(wsProtocol.getDeviceIdLow()));
		String sessionID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh()),ServerUntil.longToBytes(wsProtocol.getSessionIdLow()));
		String dir=ConstantValue.fileDir+File.separatorChar+deviceID;
		String file=dir+File.separatorChar+sessionID;
		ServerUntil.hasDirectoryWithCreate(dir);
		RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
		long fileLength = randomFile.length();
		randomFile.seek(fileLength);
		randomFile.write(wsProtocol.getContent());
		randomFile.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private void echoPackage(WsProtocol wsProtocol){
		System.out.println("--------------收到包------------------");
		System.out.println("头部："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getHead_data())));
		System.out.println("版本号："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getVersionId())));
		System.out.println("设备id高8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh())));
		System.out.println("设备id低8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getDeviceIdLow())));
		System.out.println("对应设备UUID:"+ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh()),ServerUntil.longToBytes(wsProtocol.getDeviceIdLow())));
		System.out.println("回话id高8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh())));
		System.out.println("回话id低8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSessionIdLow())));
		System.out.println("对应回话UUID:"+ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh()),ServerUntil.longToBytes(wsProtocol.getSessionIdLow())));
		System.out.println("序号Id："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSequenceId())));
		System.out.println("保留位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getReserve())));
		System.out.println("校验码："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getCheck())));
		System.out.println("数据长度:"+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getContentLength())));
		System.out.print("数据：");
		for(int i=0;i<wsProtocol.getContent().length;i++){
			System.out.print(" "+wsProtocol.getContent()[i]);
		}
		System.out.println();
	}
}
