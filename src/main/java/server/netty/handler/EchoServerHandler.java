package server.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;
import server.until.ServerUntil;
import server.until.ThreadPool;
import server.until.ThreadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EchoServerHandler extends ChannelHandlerAdapter {
	private static final Logger LOGGER=LoggerFactory.getLogger(EchoServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WsProtocol wsProtocol=(WsProtocol)msg;
		recordFileInIOThread(wsProtocol);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private void recordFileInIOThread(WsProtocol wsProtocol){
		String deviceID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh()),ServerUntil.longToBytes(wsProtocol.getDeviceIdLow()));
		String sessionID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh()),ServerUntil.longToBytes(wsProtocol.getSessionIdLow()));
		String sequenceID=String.valueOf(wsProtocol.getSequenceId());
		LOGGER.debug("收到包:"+deviceID+":"+sessionID+":"+sequenceID);
		String deviceDir= ConstantValue.fileDir+ File.separatorChar+deviceID;
		String sessionDir=deviceDir+File.separatorChar+sessionID;
		String file=sessionDir+File.separatorChar+sequenceID;
		ServerUntil.hasDirectoryWithCreate(deviceDir);
		ServerUntil.hasDirectoryWithCreate(sessionDir);
		try {
			RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
			long fileLength = randomFile.length();
			randomFile.seek(fileLength);
			randomFile.write(wsProtocol.getContent());
			randomFile.close();
		}catch(Exception e) {
			LOGGER.error("包："+deviceID+":"+sessionID+":"+sequenceID+"记录出错!");
			throw new RuntimeException(e);
		}
	}

	private void echoPackage(WsProtocol wsProtocol){
		LOGGER.debug("--------------收到包------------------");
		LOGGER.debug("头部："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getHead_data())));
		LOGGER.debug("版本号："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getVersionId())));
		LOGGER.debug("设备id高8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh())));
		LOGGER.debug("设备id低8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getDeviceIdLow())));
		LOGGER.debug("对应设备UUID:"+ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh()),ServerUntil.longToBytes(wsProtocol.getDeviceIdLow())));
		LOGGER.debug("回话id高8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh())));
		LOGGER.debug("回话id低8位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSessionIdLow())));
		LOGGER.debug("对应回话UUID:"+ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh()),ServerUntil.longToBytes(wsProtocol.getSessionIdLow())));
		LOGGER.debug("序号Id："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getSequenceId())));
		LOGGER.debug("保留位："+Arrays.toString(ServerUntil.longToBytes(wsProtocol.getReserve())));
		LOGGER.debug("校验码："+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getCheck())));
		LOGGER.debug("数据长度:"+Arrays.toString(ServerUntil.intToByteArray(wsProtocol.getContentLength())));
		LOGGER.debug("数据：");
		for(int i=0;i<wsProtocol.getContent().length;i++){
			LOGGER.debug(" "+wsProtocol.getContent()[i]);
		}
		LOGGER.debug("\n");
	}
}
